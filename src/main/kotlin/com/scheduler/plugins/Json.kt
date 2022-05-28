package com.scheduler.plugins

import io.ktor.client.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation

val serverJsonConfiguration by lazy {
    Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
}

val clientJsonConfiguration by lazy {
    Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
}

fun Application.json() {
    install(ContentNegotiation) {
        json(serverJsonConfiguration)
    }
}

fun HttpClientConfig<*>.json() {
    install(ClientContentNegotiation) {
        json(clientJsonConfiguration)
    }
}