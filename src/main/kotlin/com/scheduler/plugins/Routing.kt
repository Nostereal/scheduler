package com.scheduler.plugins

import com.scheduler.auth.authHandler
import com.scheduler.booking.bookingHandler
import com.scheduler.profile.profileHandler
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.kodein.di.DI

fun Application.configureRouting(di: DI) {
    routing {
        profileHandler(di)
        authHandler(di)
        bookingHandler(di)
    }
}
