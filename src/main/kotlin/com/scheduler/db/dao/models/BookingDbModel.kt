package com.scheduler.db.dao.models

import java.time.OffsetDateTime
import java.util.*

data class BookingDbModel(
    val uuid: UUID,
    val date: OffsetDateTime,
    val ownerId: Long,
)
