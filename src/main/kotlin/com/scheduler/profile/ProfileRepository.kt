package com.scheduler.profile

import com.scheduler.db.dao.BookingsDao
import com.scheduler.db.dao.UsersDao
import com.scheduler.db.dao.models.UserDbModel
import com.scheduler.db.tables.SystemConfig
import com.scheduler.polytech.PolytechApi
import com.scheduler.profile.models.ProfileInfo
import com.scheduler.profile.models.ProfileResponse
import com.scheduler.shared.models.ImageUrl
import kotlinx.coroutines.*
import java.time.LocalTime
import java.time.temporal.ChronoUnit

class ProfileRepository(
    private val bookingsDao: BookingsDao,
    private val usersDao: UsersDao,
    private val polytechApi: PolytechApi,
    private val appScope: CoroutineScope,
) {

    suspend fun getProfileInfo(token: String): ProfileResponse = coroutineScope {
        val dormitoryDeferred = async(Dispatchers.IO) { polytechApi.getDormInfo(token) }
        val userInfoDeferred = async(Dispatchers.IO) { polytechApi.getUserInfo(token) }

        val dormitory = dormitoryDeferred.await()
        val userInfo = userInfoDeferred.await().user
        val bookings = bookingsDao.allBookingsByUser(userInfo.id)/*.map { it.toBookingModel() }*/

        val userDbModel = UserDbModel.from(userInfo, dormitory)
        appScope.launch { usersDao.insertUserIfNotExist(userDbModel) }

        val profileInfo = ProfileInfo(
            avatar = ImageUrl(userInfo.avatar),
            fullName = dormitory.student,
            dorm = dormitory.dormNum,
            livingRoom = dormitory.dormRoom,
        )

        ProfileResponse(
            profileInfo = profileInfo,
            bookings = bookings,
        )
    }

    // todo: get profile info directly by user id

}

fun calculateSessionStartTime(config: SystemConfig, sessionNum: Short): LocalTime {
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