package com.example.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.data.api.ApiResult
import com.example.app.data.model.Location
import com.example.app.data.model.WeatherDetail
import com.example.app.data.repository.WeatherRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _uiState = MutableLiveData(WeatherUiState())
    val uiState: LiveData<WeatherUiState> = _uiState

    private var searchJob: Job? = null

    fun onEvent(event: WeatherEvent) {
        when (event) {
            is WeatherEvent.SearchLocation -> searchLocations(event.query)
            is WeatherEvent.OpenDetail -> openDetail(event.id)
            is WeatherEvent.ToggleFavorite -> toggleFavorite(event.location)
            WeatherEvent.ClearSearch -> {
                _uiState.value = state().copy(
                    searchQuery = "",
                    locations = emptyList(),
                    isEmpty = false,
                    error = null,
                    isLoading = false
                )
            }
            WeatherEvent.RefreshWeather -> {
                state().selectedLocation?.let { loadWeather(it) }
            }
        }
    }

    private fun searchLocations(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(400)

            _uiState.value = state().copy(
                searchQuery = query,
                isLoading = true,
                error = null,
                isEmpty = false
            )

            if (query.length < 2) {
                _uiState.value = state().copy(
                    locations = emptyList(),
                    isLoading = false,
                    isEmpty = false
                )
                return@launch
            }

            when (val result = repository.searchLocations(query)) {
                is ApiResult.Success -> {
                    _uiState.value = state().copy(
                        isLoading = false,
                        locations = result.data,
                        isEmpty = result.data.isEmpty(),
                        error = null
                    )
                }
                is ApiResult.Error -> {
                    _uiState.value = state().copy(
                        isLoading = false,
                        error = result.message,
                        locations = emptyList(),
                        isEmpty = false
                    )
                }
                else -> Unit
            }
        }
    }

    private fun openDetail(id: Int) {
        val s = state()
        val loc = s.locations.firstOrNull { it.id == id }
            ?: s.favorites.firstOrNull { it.id == id }

        if (loc == null) {
            _uiState.value = s.copy(
                errorDetail = "Location not found",
                weatherDetail = null,
                isLoadingDetail = false,
                selectedLocation = null
            )
            return
        }

        loadWeather(loc)
    }

    private fun loadWeather(location: Location) {
        viewModelScope.launch {
            _uiState.value = state().copy(
                selectedLocation = location,
                isLoadingDetail = true,
                errorDetail = null
            )

            when (val result = repository.getWeather(location)) {
                is ApiResult.Success -> {
                    _uiState.value = state().copy(
                        isLoadingDetail = false,
                        weatherDetail = result.data,
                        errorDetail = null
                    )
                }
                is ApiResult.Error -> {
                    _uiState.value = state().copy(
                        isLoadingDetail = false,
                        errorDetail = result.message,
                        weatherDetail = null
                    )
                }
                else -> Unit
            }
        }
    }

    private fun toggleFavorite(location: Location) {
        val s = state()
        val favorites = s.favorites.toMutableSet()

        if (favorites.contains(location)) favorites.remove(location)
        else favorites.add(location)

        _uiState.value = s.copy(favorites = favorites)
    }

    private fun state(): WeatherUiState = _uiState.value ?: WeatherUiState()
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