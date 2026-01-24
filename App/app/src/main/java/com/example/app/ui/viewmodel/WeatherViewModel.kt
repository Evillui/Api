package com.example.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.data.api.ApiResult
import com.example.app.data.model.Location
import com.example.app.data.model.WeatherDetail
import com.example.app.data.repository.WeatherRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun onEvent(event: WeatherEvent) {
        when (event) {
            is WeatherEvent.SearchLocation -> {
                searchLocations(event.query)
            }
            is WeatherEvent.SelectLocation -> {
                loadWeather(event.location)
            }
            is WeatherEvent.ToggleFavorite -> {
                toggleFavorite(event.location)
            }
            WeatherEvent.ClearSearch -> {
                _uiState.value = _uiState.value.copy(
                    searchQuery = "",
                    locations = emptyList(),
                    isEmpty = false
                )
            }
            WeatherEvent.RefreshWeather -> {
                _uiState.value.selectedLocation?.let {
                    loadWeather(it)
                }
            }
        }
    }

    private fun searchLocations(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(400)

            _uiState.value = _uiState.value.copy(
                searchQuery = query,
                isLoading = true,
                error = null
            )

            if (query.length < 2) {
                _uiState.value = _uiState.value.copy(
                    locations = emptyList(),
                    isLoading = false,
                    isEmpty = false
                )
                return@launch
            }

            when (val result = repository.searchLocations(query)) {
                is ApiResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        locations = result.data,
                        isEmpty = result.data.isEmpty()
                    )
                }
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                else -> {}
            }
        }
    }

    private fun loadWeather(location: Location) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                selectedLocation = location,
                isLoadingDetail = true,
                errorDetail = null
            )

            when (val result = repository.getWeather(location)) {
                is ApiResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoadingDetail = false,
                        weatherDetail = result.data
                    )
                }
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoadingDetail = false,
                        errorDetail = result.message
                    )
                }
                else -> {}
            }
        }
    }

    private fun toggleFavorite(location: Location) {
        val favorites = _uiState.value.favorites.toMutableSet()
        if (favorites.contains(location)) {
            favorites.remove(location)
        } else {
            favorites.add(location)
        }
        _uiState.value = _uiState.value.copy(favorites = favorites)
    }
}

data class WeatherUiState(
    val isLoading: Boolean = false,
    val locations: List<Location> = emptyList(),
    val favorites: Set<Location> = emptySet(),
    val error: String? = null,
    val searchQuery: String = "",
    val isEmpty: Boolean = false,
    val isLoadingDetail: Boolean = false,
    val weatherDetail: WeatherDetail? = null,
    val errorDetail: String? = null,
    val selectedLocation: Location? = null
)

sealed class WeatherEvent {
    data class SearchLocation(val query: String) : WeatherEvent()
    data class SelectLocation(val location: Location) : WeatherEvent()
    data class ToggleFavorite(val location: Location) : WeatherEvent()
    object ClearSearch : WeatherEvent()
    object RefreshWeather : WeatherEvent()
}