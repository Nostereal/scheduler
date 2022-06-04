package com.scheduler.booking

import com.scheduler.booking.models.CreateBookingRequest
import com.scheduler.booking.models.DeleteBookingRequest
import com.scheduler.booking.models.GetBookingsForDateRequest
import com.scheduler.config.SystemConfigRepository
import com.scheduler.shared.models.TypedResult
import com.scheduler.shared.serializer.TypedResultSerializer
import com.scheduler.shared.serializer.date.LocalDateSerializer
import com.scheduler.utils.respond
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.builtins.ListSerializer
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

    post("api/1/booking/create") {
        val requestData: CreateBookingRequest = call.receiveOrNull()
            ?: return@post call.respond(TypedResult.BadRequest("Incorrect input data"))

        val typedResult = try {
            bookingRepository.createBooking(requestData.userId, requestData.startDate, requestData.sessionNum)
        } catch (e: CancellationException) {
            if (e.message == null) {
                TypedResult.InternalError("An error occurred while creating the booking")
            } else {
                TypedResult.BadRequest(e.message!!)
            }
        }

        call.respond(typedResult)
    }

    get("api/1/bookings") {
        val requestData: GetBookingsForDateRequest = call.receiveOrNull()
            ?: return@get call.respond(TypedResult.BadRequest("Date is missing"))

        val response = try {
            bookingRepository.getBookingsByDate(requestData.date)
        } catch (e: CancellationException) {
            TypedResult.BadRequest(e.message ?: "An error occurred while fetching bookings")
        }

        call.respond(response)
    }

    get("api/1/bookings/dates") {
        val serializer = TypedResultSerializer(ListSerializer(LocalDateSerializer))
        call.respond(serializer, systemConfigRepository.getAvailableDates())
    }
}