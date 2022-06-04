package com.scheduler.booking.models

import com.scheduler.shared.serializer.date.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class CreateBookingRequest(
    val userId: Long,
    @Serializable(with = LocalDateSerializer::class)
    val startDate: LocalDate,
    val sessionNum: Short,
)
