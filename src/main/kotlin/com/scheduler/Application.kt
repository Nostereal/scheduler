package com.scheduler

import com.scheduler.auth.di.authModule
import com.scheduler.dao.DatabaseFactory
import com.scheduler.dao.DatabaseFactoryImpl
import com.scheduler.dao.initDB
import com.scheduler.di.bindAppConfig
import com.scheduler.di.bindHttpClient
import com.scheduler.plugins.configureHTTP
import com.scheduler.plugins.configureRouting
import com.scheduler.plugins.json
import com.scheduler.polytech.di.bindPolytechApi
import com.scheduler.profile.di.profileModule
import io.ktor.server.application.*
import io.ktor.server.netty.*
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    val di = DI {
        bindAppConfig(environment.config)
        bindSingleton<DatabaseFactory> { DatabaseFactoryImpl(instance()) }
        bindHttpClient()
        bindPolytechApi()

        import(profileModule)
        import(authModule)
    }
    initDB(di)
    configureHTTP()
    configureRouting(di)
    json()
}
