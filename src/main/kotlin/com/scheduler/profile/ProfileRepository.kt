package com.scheduler.profile

import com.scheduler.db.dao.BookingsDao
import com.scheduler.db.dao.UsersDao
import com.scheduler.db.dao.models.UserDbModel
import com.scheduler.db.tables.SystemConfigEntity
import com.scheduler.db.tables.fullName
import com.scheduler.polytech.PolytechApi
import com.scheduler.profile.models.ProfileInfo
import com.scheduler.profile.models.ProfileResponse
import com.scheduler.shared.models.ImageUrl
import com.scheduler.shared.models.TypedResult
import kotlinx.coroutines.*
import java.time.LocalTime
import java.time.temporal.ChronoUnit

class ProfileRepository(
    private val bookingsDao: BookingsDao,
    private val usersDao: UsersDao,
    private val polytechApi: PolytechApi,
    private val appScope: CoroutineScope,
) {

    suspend fun getProfileInfo(token: String): TypedResult<ProfileResponse> = coroutineScope {
        val dormitoryDeferred = async(Dispatchers.IO) { polytechApi.getDormInfo(token) }
        val userInfoDeferred = async(Dispatchers.IO) { polytechApi.getUserInfo(token) }

        val dormitory = dormitoryDeferred.await()
        val userInfo = userInfoDeferred.await().user
        val bookings = bookingsDao.allBookingsByUser(userInfo.id)

        val userDbModel = UserDbModel.from(userInfo, dormitory)
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

    suspend fun getProfileInfo(userId: Long): TypedResult<ProfileResponse> = coroutineScope {
        val userDef = async(Dispatchers.IO) { usersDao.getUserById(userId)!! }
        val bookingsDef = async(Dispatchers.IO) { bookingsDao.allBookingsByUser(userId) }

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
                bookings = bookingsDef.await(),
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