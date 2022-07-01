package com.scheduler.profile

import com.scheduler.db.dao.BookingsDao
import com.scheduler.db.dao.SystemConfigDao
import com.scheduler.db.dao.UsersDao
import com.scheduler.db.dao.models.UserDbModel
import com.scheduler.db.tables.SystemConfigEntity
import com.scheduler.db.tables.fullName
import com.scheduler.polytech.PolytechApi
import com.scheduler.profile.models.ProfileInfo
import com.scheduler.profile.models.ProfileResponse
import com.scheduler.shared.models.ImageUrl
import com.scheduler.shared.models.TypedResult
import com.scheduler.utils.moscowZoneId
import kotlinx.coroutines.*
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class ProfileRepository(
    private val bookingsDao: BookingsDao,
    private val usersDao: UsersDao,
    private val configDao: SystemConfigDao,
    private val polytechApi: PolytechApi,
    private val appScope: CoroutineScope,
) {

    suspend fun getProfileInfoByPolytech(token: String): TypedResult<ProfileResponse> = coroutineScope {
        val dormitoryDeferred = async(Dispatchers.IO) { polytechApi.getDormInfo(token) }
        val userInfoDeferred = async(Dispatchers.IO) { polytechApi.getUserInfo(token) }

        val dormitory = dormitoryDeferred.await()
        val userInfo = userInfoDeferred.await().user
        val bookings = bookingsDao.allBookingsByUser(userInfo.id)

        val userDbModel = UserDbModel.from(token, userInfo, dormitory)
        appScope.launch { usersDao.insertOrUpdateUser(userDbModel) }

        val profileInfo = ProfileInfo(
            avatar = userInfo.avatar?.let { ImageUrl(it) },
            fullName = dormitory.student,
            dorm = dormitory.dormNum,
            livingRoom = dormitory.dormRoom,
        )

        TypedResult.Ok(
            ProfileResponse(
                profileInfo = profileInfo,
                bookings = bookings,
            )
        )
    }

    suspend fun getProfileInfo(token: String): TypedResult<ProfileResponse> = coroutineScope {
        val user = usersDao.getUserByToken(token)
            ?: return@coroutineScope TypedResult.BadRequest("Такого пользователя не существует")
//        val userDef = async(Dispatchers.IO) { usersDao.getUserById(userId)!! }
        val now = ZonedDateTime.now(moscowZoneId)

        val firstAvailableSessionNum =
            configDao.getFirstAvailableSessionNum(date = now.toLocalDate(), time = now.toLocalTime()) ?: -1

        val bookings = bookingsDao.allUpcomingProfileBookingsByUser(
            userId = user.id.value,
            since = now.toLocalDate(),
            sinceSessionNumInclusive = firstAvailableSessionNum,
        )

        // todo: handle nullable fields
        val profileInfo = ProfileInfo(
            avatar = user.avatar?.let { ImageUrl(it) },
            fullName = user.fullName,
            dorm = user.dormNum!!,
            livingRoom = user.dormRoom!!,
        )

        TypedResult.Ok(
            ProfileResponse(
                profileInfo = profileInfo,
                bookings = bookings,
            )
        )
    }

    suspend fun getProfileInfo(userId: Long): TypedResult<ProfileResponse> = coroutineScope {
        val userDef = async(Dispatchers.IO) { usersDao.getUserById(userId)!! }
        val now = ZonedDateTime.now(moscowZoneId)

        val firstAvailableSessionNum =
            configDao.getFirstAvailableSessionNum(date = now.toLocalDate(), time = now.toLocalTime()) ?: -1

        val bookings = bookingsDao.allUpcomingProfileBookingsByUser(
            userId = userId,
            since = now.toLocalDate(),
            sinceSessionNumInclusive = firstAvailableSessionNum,
        )

        val user = userDef.await()
        // todo: handle nullable fields
        val profileInfo = ProfileInfo(
            avatar = user.avatar?.let { ImageUrl(it) },
            fullName = user.fullName,
            dorm = user.dormNum!!,
            livingRoom = user.dormRoom!!,
        )

        TypedResult.Ok(
            ProfileResponse(
                profileInfo = profileInfo,
                bookings = bookings,
            )
        )
    }

}

fun calculateSessionStartTime(config: SystemConfigEntity, sessionNum: Short): LocalTime {
    val dayStart = config.workingHoursStart
    val sessionSecs = config.sessionSeconds
    val launchStart = config.launchTimeStart

    val secsBeforeLaunch = dayStart.until(launchStart, ChronoUnit.SECONDS)
    val sessionsBeforeLaunch = secsBeforeLaunch / sessionSecs
    val sessionsLeft = sessionNum - sessionsBeforeLaunch

    return if (sessionsLeft <= 0) {
        dayStart.plusSeconds((sessionNum - 1L) * sessionSecs)
    } else {
        config.launchTimeEnd.plusSeconds((sessionsLeft - 1) * sessionSecs)
    }
}