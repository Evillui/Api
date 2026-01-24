package com.example.app.data.api

import com.example.app.stubs.GET
import com.example.app.stubs.Query
import com.example.app.data.model.LocationResponse

interface GeocodingApi {
    @GET("v1/search")
    suspend fun searchLocations(
        @Query("name") query: String,
        @Query("count") count: Int = 10,
        @Query("language") language: String = "en"
    ): LocationResponse
}