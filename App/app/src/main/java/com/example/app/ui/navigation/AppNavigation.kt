package com.example.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.app.ui.screens.FavoritesScreen
import com.example.app.ui.screens.WeatherDetailScreen
import com.example.app.ui.screens.WeatherListScreen
import com.example.app.ui.viewmodel.WeatherEvent
import com.example.app.ui.viewmodel.WeatherViewModel

object Routes {
    const val WEATHER_LIST = "weather_list"
    const val WEATHER_DETAIL = "weather_detail"
    const val FAVORITES = "favorites"
}

@Composable
fun AppNavigation(viewModel: WeatherViewModel) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Routes.WEATHER_LIST
    ) {
        composable(Routes.WEATHER_LIST) {
            WeatherListScreen(
                uiState = uiState,
                onEvent = { event ->
                    viewModel.onEvent(event)
                },
                onLocationClick = { location ->
                    viewModel.onEvent(WeatherEvent.SelectLocation(location))
                    navController.navigate(Routes.WEATHER_DETAIL)
                },
                onFavoritesClick = {
                    navController.navigate(Routes.FAVORITES)
                }
            )
        }

        composable(Routes.WEATHER_DETAIL) {
            LaunchedEffect(Unit) {
                uiState.selectedLocation?.let {
                    viewModel.onEvent(WeatherEvent.SelectLocation(it))
                }
            }

            WeatherDetailScreen(
                uiState = uiState,
                onEvent = { event ->
                    viewModel.onEvent(event)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.FAVORITES) {
            FavoritesScreen(
                uiState = uiState,
                onEvent = { event ->
                    viewModel.onEvent(event)
                },
                onLocationClick = { location ->
                    viewModel.onEvent(WeatherEvent.SelectLocation(location))
                    navController.navigate(Routes.WEATHER_DETAIL)
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}