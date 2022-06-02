package com.scheduler.booking.models

import com.scheduler.shared.serializer.ZonedDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime

@Serializable
data class GetBookingsForDateRequest(
    @Serializable(with = ZonedDateTimeSerializer::class)
    val date: ZonedDateTime
)
