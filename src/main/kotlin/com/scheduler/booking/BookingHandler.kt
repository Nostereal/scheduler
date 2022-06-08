package com.scheduler.booking

import com.scheduler.booking.models.CreateBookingRequest
import com.scheduler.booking.models.DeleteBookingRequest
import com.scheduler.config.SystemConfigRepository
import com.scheduler.shared.models.TypedResult
import com.scheduler.utils.respond
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import kotlinx.coroutines.CancellationException
import org.kodein.di.DI
import org.kodein.di.instance
import java.time.LocalDate

fun Route.bookingHandler(di: DI) {
    val bookingRepository: BookingRepository by di.instance()
    val systemConfigRepository: SystemConfigRepository by di.instance()

    delete("api/1/booking") {
        val requestData = call.receiveOrNull<DeleteBookingRequest>()
            ?: return@delete call.respond(TypedResult.BadRequest.withDefaultError)

        call.respond(
            bookingRepository.deleteBooking(requestData.id)
        )
    }

    post("api/1/booking/create") {
        val requestData: CreateBookingRequest = call.receiveOrNull()
            ?: return@post call.respond(TypedResult.BadRequest.withDefaultError)

        val typedResult = try {
            bookingRepository.createBooking(requestData.userId, requestData.date, requestData.sessionNum)
        } catch (e: CancellationException) {
            if (e.message == null) {
                TypedResult.InternalError("Произошла ошибка при создании записи, попробуйте позже")
            } else {
                TypedResult.BadRequest(e.message!!)
            }
        }

        call.respond(typedResult)
    }

    get("api/1/booking/intentionInfo") {
        val query = parseQueryString(call.request.queryString())

        val userId = query["userId"]
        val date = query["date"]
        val sessionNum = query["sessionNum"]
        if (userId.isNullOrEmpty() || date.isNullOrEmpty() || sessionNum.isNullOrEmpty()) {
            return@get call.respond(TypedResult.BadRequest.withDefaultError)
        }

        call.respond(
            bookingRepository.getBookingIntentionInfo(
                userId = userId.toLong(),
                date = LocalDate.parse(date),
                sessionNum = sessionNum.toShort(),
            )
        )
    }

    get("api/1/bookings") {
        val query = parseQueryString(call.request.queryString())

        val date = query["date"]?.let(LocalDate::parse)
            ?: return@get call.respond(TypedResult.BadRequest.withDefaultError)

        val response = try {
            bookingRepository.getBookingsByDate(date)
        } catch (e: CancellationException) {
            TypedResult.BadRequest(e.message ?: "Произошла ошибка при получении расписания")
        }

        call.respond(response)
    }

    get("api/1/bookings/dates") {
        call.respond(systemConfigRepository.getAvailableDates())
    }
}