package com.example.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.app.data.repository.WeatherRepository
import com.example.app.di.AppModule
import com.example.app.ui.navigation.AppNavigation
import com.example.app.ui.theme.WeatherAppTheme
import com.example.app.viewmodel.WeatherViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = WeatherRepository(
            geocodingApi = AppModule.geocodingApi,
            weatherApi = AppModule.weatherApi
        )
        val viewModel = WeatherViewModel(repository)

        setContent {
            WeatherAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(viewModel = viewModel)
                }
            }
        }
    }
}