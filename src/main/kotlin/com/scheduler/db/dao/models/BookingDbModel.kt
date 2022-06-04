package com.scheduler.db.dao.models

import java.time.LocalDate

data class BookingDbModel(
    val date: LocalDate,
    val sessionNum: Short,
    val ownerId: Long,
)
