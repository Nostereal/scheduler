package com.scheduler.booking.models

import com.scheduler.shared.serializer.date.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class CreateBookingRequest(
    val token: String,
    @Serializable(with = LocalDateSerializer::class)
    val date: LocalDate,
    val sessionNum: Short,
)
