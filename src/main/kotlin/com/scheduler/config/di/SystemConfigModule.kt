package com.scheduler.config.di

import com.scheduler.config.SystemConfigRepository
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

fun DI.Builder.bindSystemConfigRepository() {
    bindSingleton { SystemConfigRepository(instance(), instance()) }
}

val systemConfigModule = DI.Module(name = "systemConfig") {
    bindSystemConfigRepository()
}