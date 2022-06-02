package com.scheduler.di

import com.groupstp.isdayoff.IsDayOff
import com.groupstp.isdayoff.enums.LocalesType
import com.scheduler.AppConfig
import com.scheduler.DatabaseConfig
import com.scheduler.db.dao.DatabaseFactory
import com.scheduler.db.dao.DatabaseFactoryImpl
import com.scheduler.db.dao.di.bindBookingsDao
import com.scheduler.db.dao.di.bindSystemConfigDao
import com.scheduler.db.dao.di.bindUsersDao
import com.scheduler.plugins.json
import com.scheduler.polytech.di.bindPolytechApi
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.server.config.*
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
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

fun DI.Builder.bindHttpClient() {
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

fun DI.Builder.bindDao() {
    bindUsersDao()
    bindSystemConfigDao()
    bindBookingsDao()
}

val coreApplicationModule = DI.Module(name = "coreApplication") {
    bindSingleton<DatabaseFactory> { DatabaseFactoryImpl(instance()) }
    bindSingleton(tag = APP_SCOPE_TAG) {
        CoroutineScope(CoroutineName("application scope") + SupervisorJob() + Dispatchers.Default)
    }
    bindSingleton<IsDayOff> {
        IsDayOff.Builder()
            .setLocale(LocalesType.RUSSIA)
            .build()
    }
    bindHttpClient()
    bindPolytechApi()
    bindDao()
}

const val APP_SCOPE_TAG = "appCoroutineScope"