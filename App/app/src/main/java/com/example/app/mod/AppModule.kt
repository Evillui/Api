package com.example.app.di

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.gson.gson

object AppModule {
    val httpClient = HttpClient {
        install(ContentNegotiation) {
            gson()
        }
        install(Logging) {
            level = LogLevel.HEADERS
        }
    }
}