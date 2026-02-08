package com.example.app.viewmodel

import com.example.app.data.model.Location

sealed class WeatherEvent {
    data class SearchLocation(val query: String) : WeatherEvent()
    data class OpenDetail(val id: Int) : WeatherEvent()
    data class ToggleFavorite(val location: Location) : WeatherEvent()
    object ClearSearch : WeatherEvent()
    object RefreshWeather : WeatherEvent()
}