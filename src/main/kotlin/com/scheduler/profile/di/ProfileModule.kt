package com.scheduler.profile.di

import com.scheduler.dao.BookingDatabase
import com.scheduler.dao.BookingsDao
import com.scheduler.profile.ProfileRepository
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

fun DI.Builder.bindProfileRepositories() {
    bindSingleton { ProfileRepository(instance(), instance()) }
}

fun DI.Builder.bindProfileDao() {
    bindSingleton<BookingsDao> { BookingDatabase() }
}

val profileModule = DI.Module(name = "profile") {
    bindProfileDao()
    bindProfileRepositories()
}