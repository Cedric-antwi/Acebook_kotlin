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
    val username: String
)
data class ListUsersViewModel(
    val users: List<User>,
//    val pending: List<FriendRequest>?,
    val pendingReq: MutableList<FriendRequestViewModel>
): ViewModel