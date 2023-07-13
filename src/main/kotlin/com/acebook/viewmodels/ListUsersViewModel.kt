package com.acebook.viewmodels

import com.acebook.entities.FriendRequest
import com.acebook.entities.User
import com.acebook.schemas.FriendRequests
import com.acebook.schemas.Users
import org.http4k.template.ViewModel
import org.ktorm.dsl.Query
data class FriendRequestViewModel(
    val userID: Int,
    val friendRequestID: Int,
    val firstName: String,
    val lastName: String,
    val username: String,
    val image: String
)

data class UpdateUsersViewModel(
    val userID: Int,
    val friendRequestID: Int,
    val firstName: String,
    val lastName: String,
    val username: String,
    val image: String,
    val friendshipStatus: Boolean?
)
data class ListUsersViewModel(
    val users: MutableList<UpdateUsersViewModel>,
    val pendingReq: MutableList<FriendRequestViewModel>,
    val currentUser: User?
): ViewModel