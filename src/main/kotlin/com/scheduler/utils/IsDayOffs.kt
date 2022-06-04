package com.scheduler.utils

import com.scheduler.isdayoff.IsDayOff
import com.scheduler.isdayoff.IsDayOffDateType
import com.scheduler.isdayoff.enums.DayType
import java.time.LocalDate
import java.time.ZonedDateTime
import java.util.*

suspend fun IsDayOff.getDayType(date: ZonedDateTime): DayType = dayType(Date.from(date.toInstant()))

suspend fun IsDayOff.getDayType(date: LocalDate): DayType = dayType(
    Date.from(date.atStartOfDay(moscowZoneId).toInstant())
)

suspend fun IsDayOff.isDayWorking(date: ZonedDateTime): Boolean {
    val dayType = getDayType(date)
    return dayType.isWorkingDay == true
}

suspend fun IsDayOff.isDayWorking(date: LocalDate): Boolean {
    val dayType = getDayType(date)
    return dayType.isWorkingDay == true
}

suspend fun IsDayOff.dayTypesByRange(start: LocalDate, end: LocalDate): List<IsDayOffDateType> {
    return daysTypeByRange(
        start.toJavaDate(),
        end.toJavaDate()
    )
}
