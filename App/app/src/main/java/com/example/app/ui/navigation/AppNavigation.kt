package com.example.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.app.ui.screens.FavoritesScreen
import com.example.app.ui.screens.WeatherDetailScreen
import com.example.app.ui.screens.WeatherListScreen
import com.example.app.viewmodel.WeatherEvent
import com.example.app.viewmodel.WeatherViewModel

object Routes {
    const val WEATHER_LIST = "weather_list"
    const val WEATHER_DETAIL = "weather_detail/{id}"
    const val FAVORITES = "favorites"

    fun detail(id: Int) = "weather_detail/$id"
}

@Composable
fun AppNavigation(viewModel: WeatherViewModel) {
    val navController = rememberNavController()
    val uiState = viewModel.uiState

    NavHost(
        navController = navController,
        startDestination = Routes.WEATHER_LIST
    ) {
        composable(Routes.WEATHER_LIST) {
            WeatherListScreen(
                uiState = uiState,
                onEvent = viewModel::onEvent,
                onLocationClick = { location ->
                    navController.navigate(Routes.detail(location.id))
                },
                onFavoritesClick = {
                    navController.navigate(Routes.FAVORITES)
                }
            )
        }

        composable(
            route = Routes.WEATHER_DETAIL,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: return@composable

            LaunchedEffect(id) {
                viewModel.onEvent(WeatherEvent.OpenDetail(id))
            }

            WeatherDetailScreen(
                uiState = uiState,
                onEvent = viewModel::onEvent,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.FAVORITES) {
            FavoritesScreen(
                uiState = uiState,
                onEvent = viewModel::onEvent,
                onLocationClick = { location ->
                    navController.navigate(Routes.detail(location.id))
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}