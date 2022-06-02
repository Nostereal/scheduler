package com.scheduler.db.dao.di

import com.scheduler.db.dao.*
import org.kodein.di.DI
import org.kodein.di.bindSingleton

fun DI.Builder.bindBookingsDao() {
    bindSingleton<BookingsDao> { BookingDatabase() }
}

fun DI.Builder.bindUsersDao() {
    bindSingleton<UsersDao>(overrides = false) { UsersDatabase() }
}

fun DI.Builder.bindSystemConfigDao() {
    bindSingleton<SystemConfigDao>() { SystemConfigDatabase() }
}