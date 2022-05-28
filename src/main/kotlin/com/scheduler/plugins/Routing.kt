package com.scheduler.plugins

import com.scheduler.auth.authorize
import com.scheduler.profile.getProfile
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.kodein.di.DI

fun Application.configureRouting(di: DI) {
    routing {
        getProfile(di)
        authorize(di)
    }
}
