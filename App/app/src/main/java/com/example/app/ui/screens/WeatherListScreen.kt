package com.example.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.app.ui.components.ErrorState
import com.example.app.ui.components.LocationItem
import com.example.app.ui.components.LoadingState
import com.example.app.ui.viewmodel.WeatherEvent
import com.example.app.ui.viewmodel.WeatherUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherListScreen(
    uiState: WeatherUiState,
    onEvent: (WeatherEvent) -> Unit,
    onLocationClick: (com.example.app.data.model.Location) -> Unit,
    onFavoritesClick: () -> Unit
) {
    var searchQuery by remember { mutableStateOf(uiState.searchQuery) }

    LaunchedEffect(searchQuery) {
        onEvent(WeatherEvent.SearchLocation(searchQuery))
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Weather App",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                actions = {
                    IconButton(onClick = { onEvent(WeatherEvent.RefreshWeather) }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                    IconButton(onClick = onFavoritesClick) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = "Favorites",
                            tint = if (uiState.favorites.isNotEmpty())
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Поле поиска
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search city...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = {
                            searchQuery = ""
                            onEvent(WeatherEvent.ClearSearch)
                        }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = MaterialTheme.shapes.large
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Контент
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    uiState.isLoading -> {
                        LoadingState()
                    }

                    uiState.error != null -> {
                        ErrorState(
                            message = uiState.error,
                            onRetry = { onEvent(WeatherEvent.SearchLocation(searchQuery)) }
                        )
                    }

                    uiState.isEmpty -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                "No locations found",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            if (searchQuery.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(onClick = {
                                    searchQuery = ""
                                    onEvent(WeatherEvent.ClearSearch)
                                }) {
                                    Text("Clear search")
                                }
                            }
                        }
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(uiState.locations) { location ->
                                LocationItem(
                                    location = location,
                                    isFavorite = uiState.favorites.contains(location),
                                    onClick = { onLocationClick(location) },
                                    onFavoriteClick = {
                                        onEvent(WeatherEvent.ToggleFavorite(location))
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}