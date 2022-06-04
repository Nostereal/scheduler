package com.scheduler.booking.models

import com.scheduler.shared.serializer.date.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class GetBookingsForDateRequest(
    @Serializable(with = LocalDateSerializer::class)
    val date: LocalDate
)
