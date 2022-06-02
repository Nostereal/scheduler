package com.scheduler.booking

import com.scheduler.booking.models.CreateBookingRequest
import com.scheduler.booking.models.DeleteBookingRequest
import com.scheduler.booking.models.GetBookingsForDateRequest
import com.scheduler.config.SystemConfigRepository
import com.scheduler.shared.models.TypedResult
import com.scheduler.utils.respond
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import org.kodein.di.DI
import org.kodein.di.instance

fun Route.bookingHandler(di: DI) {
    val bookingRepository: BookingRepository by di.instance()
    val systemConfigRepository: SystemConfigRepository by di.instance()

    delete("api/1/booking") {
        val requestData = call.receiveOrNull<DeleteBookingRequest>()
            ?: return@delete call.respond(TypedResult.BadRequest("Booking id is missing"))

        call.respond(
            bookingRepository.deleteBooking(requestData.id)
        )
    }

    post("api/1/booking/add") {
        val requestData: CreateBookingRequest = call.receiveOrNull()
            ?: return@post call.respond(TypedResult.BadRequest("Incorrect input data"))

        call.respond(
            bookingRepository.createBooking(requestData.userId, requestData.startDate)
        )
    }

    get("api/1/bookings") {
        val requestData: GetBookingsForDateRequest = call.receiveOrNull()
            ?: return@get call.respond(TypedResult.BadRequest("Date is missing"))

        call.respond(bookingRepository.getBookingsByDate(requestData.date))
    }

    get("api/1/bookings/dates") {
        call.respond(systemConfigRepository.getAvailableDates())
    }
}