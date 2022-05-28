package com.scheduler.di

import com.scheduler.AppConfig
import com.scheduler.DatabaseConfig
import com.scheduler.plugins.json
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.server.config.*
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

fun DI.MainBuilder.bindAppConfig(config: ApplicationConfig) {
    val databaseConfig = DatabaseConfig(
        driverClassName = config.property("storage.driverClassName").getString(),
        jdbcUrl = config.property("storage.jdbcURL").getString(),
    )

    bindSingleton { databaseConfig }
    bindSingleton { AppConfig(instance()) }
}

fun DI.MainBuilder.bindHttpClient() {
    bindSingleton {
        HttpClient(CIO) {
            json()
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }
            defaultRequest {
                url("https://e.mospolytech.ru/")
                header("Host", "e.mospolytech.ru")
            }
        }

    }
}
