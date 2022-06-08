package com.scheduler.booking.di

import com.scheduler.booking.BookingRepository
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

fun DI.Builder.bindBookingRepository() {
    bindSingleton { BookingRepository(instance(), instance(), instance(), instance()) }
}

val bookingModule = DI.Module(name = "booking") {
    bindBookingRepository()
}