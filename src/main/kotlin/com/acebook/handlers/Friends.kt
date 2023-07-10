package com.acebook.handlers

import com.acebook.*
import com.acebook.entities.FriendRequest
import com.acebook.entities.User
import com.acebook.schemas.Users
import com.acebook.templateRenderer
import com.acebook.viewmodels.ListUsersViewModel
import org.http4k.core.*
import org.ktorm.entity.add
import org.ktorm.entity.sequenceOf
import org.ktorm.entity.toList


fun listUsers(): HttpHandler = {
    val users = database.sequenceOf(Users).toList()
    val viewModel = ListUsersViewModel(users)
    Response(Status.OK).body(templateRenderer(viewModel))
}

fun friendRequest(contexts: RequestContexts, request: Request, friendId: Int): Response {
    val currentUser: User? = contexts[request]["user"]
    val mySenderId = currentUser?.id //our id
    val friendReceiverId = friendId // friends id

    val newFriendRequest = FriendRequest {
            if (mySenderId != null) {
                senderId = mySenderId
            }
        receiverId = friendReceiverId
        requestStatus = false
    }

    database.sequenceOf(com.acebook.schemas.FriendRequests).add(newFriendRequest)
    return Response(Status.SEE_OTHER)
        .header("Location", "/")
        .body("")
}

