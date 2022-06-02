package com.scheduler.booking.models

import com.scheduler.profile.models.Booking
import com.scheduler.shared.serializer.ZonedDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime

@Serializable
data class BookingsForDateResponse(
    val bookingWindows: List<BookingsForTimeWindow>,
)

@Serializable
data class BookingsForTimeWindow(
    @Serializable(with = ZonedDateTimeSerializer::class)
    val startDate: ZonedDateTime,
    val bookings: List<Booking>,
)
