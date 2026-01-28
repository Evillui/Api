package com.example.app.di

import com.example.app.data.api.GeocodingApi
import com.example.app.data.api.WeatherApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object AppModule {

    private val okHttp = OkHttpClient.Builder().build()

    private val geocodingRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://geocoding-api.open-meteo.com/")
        .client(okHttp)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val weatherRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.open-meteo.com/")
        .client(okHttp)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val geocodingApi: GeocodingApi = geocodingRetrofit.create(GeocodingApi::class.java)
    val weatherApi: WeatherApi = weatherRetrofit.create(WeatherApi::class.java)
}