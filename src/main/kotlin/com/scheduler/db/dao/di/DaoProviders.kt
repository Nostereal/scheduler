package com.scheduler.db.dao.di

import com.scheduler.db.dao.BookingDatabase
import com.scheduler.db.dao.BookingsDao
import com.scheduler.db.dao.UsersDao
import com.scheduler.db.dao.UsersDatabase
import org.kodein.di.DI
import org.kodein.di.bindSingleton

fun DI.Builder.bindBookingsDao() {
    bindSingleton<BookingsDao> { BookingDatabase() }
}

fun DI.Builder.bindUsersDao() {
    bindSingleton<UsersDao> { UsersDatabase() }
}