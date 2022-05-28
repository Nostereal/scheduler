package com.scheduler.polytech.di

import com.scheduler.polytech.PolytechApi
import com.scheduler.polytech.PolytechApiImpl
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance


fun DI.Builder.bindPolytechApi() {
    bindSingleton<PolytechApi> { PolytechApiImpl(instance()) }
}