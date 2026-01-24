package com.example.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.app.ui.components.ErrorState
import com.example.app.ui.components.LoadingState
import com.example.app.ui.viewmodel.WeatherEvent
import com.example.app.ui.viewmodel.WeatherUiState
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherDetailScreen(
    uiState: WeatherUiState,
    onEvent: (WeatherEvent) -> Unit,
    onBack: () -> Unit
) {
    val weatherDetail = uiState.weatherDetail
    val location = uiState.selectedLocation

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        location?.displayName ?: "Weather",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (location != null) {
                        IconButton(
                            onClick = {
                                onEvent(WeatherEvent.ToggleFavorite(location))
                            }
                        ) {
                            Icon(
                                imageVector = if (uiState.favorites.contains(location))
                                    Icons.Filled.Favorite
                                else
                                    Icons.Outlined.Favorite,
                                contentDescription = "Toggle favorite",
                                tint = if (uiState.favorites.contains(location))
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        when {
            uiState.isLoadingDetail -> {
                LoadingState(message = "Loading weather data...")
            }

            uiState.errorDetail != null -> {
                ErrorState(
                    message = uiState.errorDetail ?: "Error loading weather",
                    onRetry = {
                        location?.let { onEvent(WeatherEvent.SelectLocation(it)) }
                    }
                )
            }

            weatherDetail == null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No weather data available")
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Текущая погода
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${weatherDetail.current.temperature.roundToInt()}°C",
                                style = MaterialTheme.typography.displayLarge.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = getWeatherDescription(weatherDetail.current.weathercode),
                                style = MaterialTheme.typography.titleLarge
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                WeatherInfoItem(
                                    title = "Wind",
                                    value = "${weatherDetail.current.windspeed.roundToInt()} km/h"
                                )
                                WeatherInfoItem(
                                    title = "Direction",
                                    value = "${weatherDetail.current.winddirection}°"
                                )
                                WeatherInfoItem(
                                    title = "Day/Night",
                                    value = if (weatherDetail.current.isDay == 1) "Day" else "Night"
                                )
                            }
                        }
                    }

                    // Почасовой прогноз
                    Text(
                        text = "Hourly Forecast",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    HourlyForecastList(
                        forecasts = weatherDetail.hourlyForecast.take(12),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    // Прогноз на неделю
                    Text(
                        text = "7-Day Forecast",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    DailyForecastList(
                        forecasts = weatherDetail.dailyForecast.take(7),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun WeatherInfoItem(title: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun HourlyForecastList(
    forecasts: List<com.example.app.data.model.HourlyForecast>,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(forecasts, key = { it.time }) { forecast ->
            HourlyForecastItem(forecast = forecast)
        }
    }
}

@Composable
fun HourlyForecastItem(forecast: com.example.app.data.model.HourlyForecast) {
    Card(
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (forecast.time.length > 16) forecast.time.substring(11, 16) else forecast.time,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "${forecast.temperature.roundToInt()}°",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = getWeatherIcon(forecast.weatherCode),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun DailyForecastList(
    forecasts: List<com.example.app.data.model.DailyForecast>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        forecasts.forEach { forecast ->
            DailyForecastItem(forecast = forecast)
        }
    }
}

@Composable
fun DailyForecastItem(forecast: com.example.app.data.model.DailyForecast) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (forecast.date.length > 5) forecast.date.substring(5) else forecast.date,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = getWeatherIcon(forecast.weatherCode),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${forecast.maxTemp.roundToInt()}°",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${forecast.minTemp.roundToInt()}°",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

fun getWeatherDescription(code: Int): String {
    return when (code) {
        0 -> "Clear sky"
        1, 2, 3 -> "Partly cloudy"
        45, 48 -> "Foggy"
        51, 53, 55 -> "Drizzle"
        56, 57 -> "Freezing drizzle"
        61, 63, 65 -> "Rain"
        66, 67 -> "Freezing rain"
        71, 73, 75 -> "Snow"
        77 -> "Snow grains"
        80, 81, 82 -> "Rain showers"
        85, 86 -> "Snow showers"
        95 -> "Thunderstorm"
        96, 99 -> "Thunderstorm with hail"
        else -> "Unknown"
    }
}

fun getWeatherIcon(code: Int): String {
    return when (code) {
        0 -> "☀️"
        1, 2, 3 -> "⛅"
        45, 48 -> "🌫️"
        51, 53, 55 -> "🌧️"
        56, 57 -> "🌨️"
        61, 63, 65 -> "🌧️"
        66, 67 -> "🌨️"
        71, 73, 75 -> "❄️"
        77 -> "❄️"
        80, 81, 82 -> "🌦️"
        85, 86 -> "❄️"
        95 -> "⛈️"
        96, 99 -> "⛈️"
        else -> "?"
    }
}