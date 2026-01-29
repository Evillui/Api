package com.example.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app.data.repository.WeatherRepository
import com.example.app.di.AppModule
import com.example.app.ui.navigation.AppNavigation
import com.example.app.ui.theme.WeatherAppTheme
import com.example.app.viewmodel.WeatherViewModel
import com.example.app.viewmodel.WeatherViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = WeatherRepository(
            geocodingApi = AppModule.geocodingApi,
            weatherApi = AppModule.weatherApi
        )
        val factory = WeatherViewModelFactory(repository)

        setContent {
            WeatherAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val weatherVM: WeatherViewModel = viewModel(factory = factory)
                    AppNavigation(viewModel = weatherVM)
                }
            }
        }
    }
}