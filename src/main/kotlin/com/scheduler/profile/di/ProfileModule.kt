package com.scheduler.profile.di

import com.scheduler.di.APP_SCOPE_TAG
import com.scheduler.profile.ProfileRepository
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

fun DI.Builder.bindProfileRepositories() {
    bindSingleton { ProfileRepository(instance(), instance(), instance(), instance(), appScope = instance(APP_SCOPE_TAG)) }
}

val profileModule = DI.Module(name = "profile") {
    bindProfileRepositories()
}