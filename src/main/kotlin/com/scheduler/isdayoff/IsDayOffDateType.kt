package com.scheduler.isdayoff

import com.scheduler.isdayoff.enums.DayType
import java.util.*

data class IsDayOffDateType(
    var date: Date,
    /**
     * Тип дня
     * @see com.groupstp.isdayoff.enums.DayType
     */
    var dayType: DayType,
)