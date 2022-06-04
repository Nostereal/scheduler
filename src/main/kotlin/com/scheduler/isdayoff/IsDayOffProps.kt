package com.scheduler.isdayoff

import com.scheduler.isdayoff.enums.LocalesType

class IsDayOffProps(builder: IsDayOffBuilder) {
    val locale: LocalesType = builder.locale
    val preHolidaysDay: Int = builder.preHolidaysDay
    val sixDaysWorkWeek: Int = builder.sixDaysWorkWeek
    val covidWorkingDays: Int = builder.covidWorkingDays
}