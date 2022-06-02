package com.scheduler.utils

import com.groupstp.isdayoff.IsDayOff
import com.groupstp.isdayoff.enums.DayType
import java.time.ZonedDateTime
import java.util.*

fun IsDayOff.getDayType(date: ZonedDateTime): DayType = dayType(Date.from(date.toInstant()))

fun IsDayOff.isDayWorking(date: ZonedDateTime): Boolean {
    val dayType = getDayType(date)
    return dayType.isWorkingDay == true
}
