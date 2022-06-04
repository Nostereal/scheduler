package com.scheduler.db.dao.models

import com.scheduler.db.tables.BookingEntity
import java.time.LocalDate
import java.util.*

data class BookingDbModel(
    val uuid: UUID,
    val date: LocalDate,
    val sessionNum: Short,
    val owner: UserDbModel,
) {

    companion object {
        fun from(entity: BookingEntity) = with(entity) {
            BookingDbModel(
                uuid = id.value,
                date = date,
                sessionNum = sessionNum,
                owner = UserDbModel.from(owner),
            )
        }
    }

}
