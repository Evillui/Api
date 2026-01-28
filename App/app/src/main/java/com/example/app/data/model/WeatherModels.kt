package com.example.app.data.model

import com.google.gson.annotations.SerializedName

data class LocationResponse(
    val results: List<Location>
)

data class Location(
    val id: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String,
    @SerializedName("admin1") val region: String?,
    val timezone: String
) {
    val displayName: String
        get() = "$name, ${region ?: country}"
}

data class WeatherResponse(
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    @SerializedName("current_weather") val currentWeather: CurrentWeather,
    val hourly: HourlyWeather,
    val daily: DailyWeather
)

data class CurrentWeather(
    val time: String,
    val temperature: Double,
    val weathercode: Int,
    val windspeed: Double,
    val winddirection: Int,
    @SerializedName("is_day") val isDay: Int
)

data class HourlyWeather(
    val time: List<String>,
    @SerializedName("temperature_2m") val temperature: List<Double>,
    val weathercode: List<Int>,
    @SerializedName("windspeed_10m") val windspeed: List<Double>
)

data class DailyWeather(
    val time: List<String>,
    @SerializedName("temperature_2m_max") val tempMax: List<Double>,
    @SerializedName("temperature_2m_min") val tempMin: List<Double>,
    val weathercode: List<Int>
)

data class WeatherDetail(
    val location: Location,
    val current: CurrentWeather,
    val hourlyForecast: List<HourlyForecast>,
    val dailyForecast: List<DailyForecast>
)

data class HourlyForecast(
    val time: String,
    val temperature: Double,
    val weatherCode: Int,
    val windSpeed: Double
)

data class DailyForecast(
    val date: String,
    val maxTemp: Double,
    val minTemp: Double,
    val weatherCode: Int
)