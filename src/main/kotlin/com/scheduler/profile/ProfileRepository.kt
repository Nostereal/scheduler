package com.scheduler.profile

import com.scheduler.db.dao.BookingsDao
import com.scheduler.db.dao.UsersDao
import com.scheduler.db.dao.models.UserDbModel
import com.scheduler.polytech.PolytechApi
import com.scheduler.profile.models.Booking
import com.scheduler.profile.models.ProfileInfo
import com.scheduler.profile.models.ProfileResponse
import com.scheduler.profile.models.TimeBracket
import com.scheduler.shared.models.ImageUrl
import kotlinx.coroutines.*
import java.time.OffsetDateTime
import java.time.ZoneOffset

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
        val bookings = bookingsDao.allBookingsByUser(userInfo.id).map { it.toBookingModel() }

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

fun com.scheduler.db.tables.Booking.toBookingModel(): Booking {
    val startTime = OffsetDateTime.of(date, ZoneOffset.UTC)
    return Booking(
        id = id.value,
        timeBracket = TimeBracket(
            start = startTime,
            end = startTime.plusSeconds(configVersion.sessionSeconds.toLong()),
        )
    )
}