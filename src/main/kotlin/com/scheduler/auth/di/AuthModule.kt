package com.scheduler.auth.di

import com.scheduler.auth.AuthRepository
import com.scheduler.db.dao.di.bindUsersDao
import com.scheduler.di.APP_SCOPE_TAG
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

val authModule = DI.Module(name = "auth") {
    bindUsersDao()
    bindSingleton { AuthRepository(instance(), instance(), appScope = instance(APP_SCOPE_TAG)) }
}