package com.scheduler.db.dao.models

import java.time.LocalDate
import java.time.LocalTime

data class BookingDbModel(
    val date: LocalDate,
    val time: LocalTime,
    val ownerId: Long,
)
