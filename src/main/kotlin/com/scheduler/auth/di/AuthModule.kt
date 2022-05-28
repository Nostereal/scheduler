package com.scheduler.auth.di

import com.scheduler.auth.AuthRepository
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

val authModule = DI.Module(name = "auth") {
    bindSingleton { AuthRepository(instance()) }
}