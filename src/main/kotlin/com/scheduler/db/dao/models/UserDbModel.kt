package com.scheduler.db.dao.models

import com.scheduler.db.tables.UserEntity
import com.scheduler.polytech.models.PolytechPaymentsResponse
import com.scheduler.polytech.models.PolytechUserResponse

data class UserDbModel(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val middleName: String?,
    val dormNum: String?,
    val dormRoom: String?,
    val isAdmin: Boolean,
) {
    companion object {
        fun from(userInfo: PolytechUserResponse.User, dormitory: PolytechPaymentsResponse.Contracts.Dormitory) =
            UserDbModel(
                id = userInfo.id,
                firstName = userInfo.firstName,
                lastName = userInfo.lastName,
                middleName = userInfo.middleName,
                dormNum = dormitory.dormNum,
                dormRoom = dormitory.dormRoom,
                isAdmin = userInfo.userStatus != PolytechUserResponse.User.Status.STUDENT, // todo: recognize admin
            )

        fun from(entity: UserEntity) = with(entity) {
            UserDbModel(
                id = id.value,
                firstName = firstName,
                lastName = lastName,
                middleName = middleName,
                dormNum = dormNum,
                dormRoom = dormRoom,
                isAdmin = isAdmin,
            )
        }
    }
}
