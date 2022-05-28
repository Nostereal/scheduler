package com.scheduler

import com.scheduler.auth.di.authModule
import com.scheduler.db.dao.initDB
import com.scheduler.di.bindAppConfig
import com.scheduler.di.coreApplicationModule
import com.scheduler.plugins.configureHTTP
import com.scheduler.plugins.configureRouting
import com.scheduler.plugins.json
import com.scheduler.profile.di.profileModule
import io.ktor.server.application.*
import io.ktor.server.netty.*
import org.kodein.di.DI

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    val di = DI {
        bindAppConfig(environment.config)
        importAll(
            coreApplicationModule,
            profileModule,
            authModule,
        )
    }
    initDB(di)
    configureHTTP()
    configureRouting(di)
    json()
}
