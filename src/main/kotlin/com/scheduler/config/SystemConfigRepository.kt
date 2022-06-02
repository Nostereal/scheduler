package com.scheduler.config

import com.groupstp.isdayoff.IsDayOff
import com.scheduler.db.dao.SystemConfigDao
import com.scheduler.shared.models.TypedResult
import com.scheduler.utils.moscowZoneId
import com.scheduler.utils.toJavaDate
import com.scheduler.utils.toZonedDateTime
import java.time.ZonedDateTime

class SystemConfigRepository(
    private val systemConfigDao: SystemConfigDao,
    private val isDayOff: IsDayOff,
) {

    suspend fun getAvailableDates(): TypedResult<List<ZonedDateTime>> {
        val now = ZonedDateTime.now(moscowZoneId)
        val config = systemConfigDao.getConfigForDate(now)

        val dates = isDayOff.daysTypeByRange(
            now.toJavaDate(),
            now.plusDays(config.maxDaysAhead.toLong()).toJavaDate()
        )
            .filter { it.dayType.isWorkingDay == true }
            .map { it.date.toZonedDateTime() }

        return TypedResult.Ok(dates)
    }

}