package com.scheduler.db.dao

import io.ktor.server.application.*
import org.kodein.di.DI
import org.kodein.di.instance

fun Application.initDB(di: DI) {
    val dbFactory: DatabaseFactory by di.instance()
    dbFactory.connect()
}