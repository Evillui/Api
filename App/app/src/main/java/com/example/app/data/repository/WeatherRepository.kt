package com.example.app.data.repository

import com.example.app.data.api.ApiResult
import com.example.app.data.model.*
import com.example.app.di.AppModule
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.delay

class WeatherRepository {
    private val client = AppModule.httpClient

    suspend fun searchLocations(query: String): ApiResult<List<Location>> {
        return try {
            println("DEBUG: Searching for: $query")

            val response = client.get("https://geocoding-api.open-meteo.com/v1/search") {
                parameter("name", query)
                parameter("count", 10)
                parameter("language", "en")
            }

            if (response.status == HttpStatusCode.OK) {
                val locationResponse = response.body<LocationResponse>()
                println("DEBUG: Found ${locationResponse.results.size} locations")

                if (locationResponse.results.isEmpty()) {
                    ApiResult.Error("No locations found")
                } else {
                    ApiResult.Success(locationResponse.results)
                }
            } else {
                ApiResult.Error("Server error: ${response.status}")
            }
        } catch (e: Exception) {
            println("DEBUG: Error: ${e.message}")
            ApiResult.Error("Network error: ${e.message}")
        }
    }

    suspend fun getWeather(location: Location): ApiResult<WeatherDetail> {
        return try {
            println("DEBUG: Getting weather for ${location.name}")

            delay(500)

            val response = client.get("https://api.open-meteo.com/v1/forecast") {
                parameter("latitude", location.latitude)
                parameter("longitude", location.longitude)
                parameter("current_weather", true)
                parameter("hourly", "temperature_2m,weathercode,windspeed_10m")
                parameter("daily", "temperature_2m_max,temperature_2m_min,weathercode")
                parameter("timezone", "auto")
            }

            if (response.status == HttpStatusCode.OK) {
                val weatherResponse = response.body<WeatherResponse>()
                println("DEBUG: Weather data received")

                // Преобразование в UI модель
                val hourlyForecast = weatherResponse.hourly.time.take(24).mapIndexed { index, time ->
                    HourlyForecast(
                        time = time,
                        temperature = weatherResponse.hourly.temperature.getOrNull(index) ?: 0.0,
                        weatherCode = weatherResponse.hourly.weathercode.getOrNull(index) ?: 0,
                        windSpeed = weatherResponse.hourly.windspeed.getOrNull(index) ?: 0.0
                    )
                }

                val dailyForecast = weatherResponse.daily.time.mapIndexed { index, date ->
                    DailyForecast(
                        date = date,
                        maxTemp = weatherResponse.daily.tempMax.getOrNull(index) ?: 0.0,
                        minTemp = weatherResponse.daily.tempMin.getOrNull(index) ?: 0.0,
                        weatherCode = weatherResponse.daily.weathercode.getOrNull(index) ?: 0
                    )
                }

                val weatherDetail = WeatherDetail(
                    location = location,
                    current = weatherResponse.currentWeather,
                    hourlyForecast = hourlyForecast,
                    dailyForecast = dailyForecast
                )

                ApiResult.Success(weatherDetail)
            } else {
                ApiResult.Error("Failed to load weather: ${response.status}")
            }
        } catch (e: Exception) {
            println("DEBUG: Weather error: ${e.message}")
            ApiResult.Error("Failed to load weather: ${e.message}")
        }
    }
}