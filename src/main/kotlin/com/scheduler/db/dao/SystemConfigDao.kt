package com.scheduler.db.dao

import com.scheduler.db.dao.utils.dbQuery
import com.scheduler.db.tables.SystemConfigEntity
import com.scheduler.db.tables.SystemConfigsTable
import com.scheduler.utils.moscowZoneId
import org.jetbrains.exposed.sql.SortOrder
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import kotlin.math.ceil

interface SystemConfigDao {

    suspend fun getConfigForDate(date: LocalDate): SystemConfigEntity

    suspend fun getFirstAvailableSessionNum(date: LocalDate, time: LocalTime): Short? // todo: or zoned date time?

    suspend fun getFirstAvailableSessionNum(date: LocalDate, time: LocalTime, config: SystemConfigEntity): Short? // todo: or zoned date time?

}

class SystemConfigDatabase : SystemConfigDao {

    override suspend fun getConfigForDate(date: LocalDate): SystemConfigEntity = dbQuery {
        val configs = SystemConfigEntity.all().orderBy(SystemConfigsTable.id to SortOrder.DESC).toList()

        val actualConfig = configs.first()
        if (date >= actualConfig.createdAt) return@dbQuery actualConfig

        for (i in 0 until configs.lastIndex) {
            val newerConfig = configs[i]
            val olderConfig = configs[i + 1]

            if (date == newerConfig.createdAt) {
                return@dbQuery newerConfig
            } else if (date < newerConfig.createdAt && date >= olderConfig.createdAt) {
                return@dbQuery olderConfig
            }
        }

        throw IllegalStateException("Didn't found a config for the date $date")
    }

    override suspend fun getFirstAvailableSessionNum(date: LocalDate, time: LocalTime): Short? {
        val config = getConfigForDate(date)

        return getFirstAvailableSessionNum(date, time, config)
    }

    override suspend fun getFirstAvailableSessionNum(date: LocalDate, time: LocalTime, config: SystemConfigEntity): Short? {
        val now = ZonedDateTime.now(moscowZoneId).toLocalDate()
        if (date > now) return 1
        if (date < now) return null

        val dayStart = config.workingHoursStart
        val dayEnd = config.workingHoursEnd
        val launchStart = config.launchTimeStart
        val launchEnd = config.launchTimeEnd

        if (time.isBefore(dayStart)) return 1
        if (time.isAfter(dayEnd)) return null

        val sessionSecs = config.sessionSeconds

        return when {
            time.isBefore(launchStart) -> {
                val secsBeforeTime = dayStart.until(time, ChronoUnit.SECONDS)
                ceil(secsBeforeTime / sessionSecs.toFloat()).toInt().toShort()
            }
            time.isAfter(launchEnd) -> {
                val launchUntilTimeSecs = launchEnd.until(time, ChronoUnit.SECONDS)
                val sessionsBeforeLaunch = dayStart.until(launchStart, ChronoUnit.SECONDS) / sessionSecs
                (sessionsBeforeLaunch + ceil(launchUntilTimeSecs / sessionSecs.toFloat())).toInt().toShort()
            }
            time.isAfter(launchStart) && time.isBefore(launchEnd) -> {
                val sessionBeforeLaunch = dayStart.until(launchStart, ChronoUnit.SECONDS) / sessionSecs
                (sessionBeforeLaunch + 1).toShort()
            }
            else -> throw IllegalStateException("impossible date and time")
        }
    }
}