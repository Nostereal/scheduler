package com.scheduler.profile

import com.scheduler.dao.BookingsDao
import com.scheduler.polytech.PolytechApi
import com.scheduler.profile.models.Booking
import com.scheduler.profile.models.ProfileInfo
import com.scheduler.profile.models.ProfileResponse
import com.scheduler.profile.models.TimeBracket
import com.scheduler.shared.models.ImageUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.time.Instant
import java.time.ZoneOffset
import java.util.*

class ProfileRepository(
    private val bookingsDao: BookingsDao,
    private val polytechApi: PolytechApi,
//    private val usersDao: UsersDao,
) {

    suspend fun getProfileInfo(token: String): ProfileResponse = coroutineScope {
        val dormitoryDeferred = async(Dispatchers.IO) { polytechApi.getDormInfo(token) }
        val userInfoDeferred = async(Dispatchers.IO) { polytechApi.getUserInfo(token) }

//      todo: val userBookings = bookingsDao.allBookingsByUser(userId)
        val now = Instant.now().atOffset(ZoneOffset.of("+03"))
        val bookings = listOf(
            Booking(
                id = UUID.randomUUID().toString(),
                timeBracket = TimeBracket(
                    now,
                    now.plusMinutes(45),
                ),
            )
        )

        val dormitory = dormitoryDeferred.await()
        val userInfo = userInfoDeferred.await()
        val profileInfo = ProfileInfo(
            avatar = ImageUrl(userInfo.user.avatar),
            fullName = dormitory.student,
            dorm = dormitory.dormNum,
            livingRoom = dormitory.dormRoom,
        )


        ProfileResponse(
            profileInfo = profileInfo,
            bookings = bookings,
        )
    }

}