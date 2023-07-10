package com.acebook.handlers

import com.acebook.*
import com.acebook.entities.FriendRequest
import com.acebook.entities.User
import com.acebook.schemas.FriendRequests
import com.acebook.schemas.Users
import com.acebook.templateRenderer
import com.acebook.viewmodels.ListUsersViewModel
import org.http4k.core.*
import org.ktorm.entity.add
import org.ktorm.entity.filter
import org.ktorm.entity.sequenceOf
import org.ktorm.entity.toList
import org.ktorm.database.*
import org.ktorm.dsl.*

//import io.reactivex.Completable


//fun createUserHandler(): HttpHandler = { request: Request ->
fun listUsers(contexts: RequestContexts): HttpHandler = { request: Request ->
val currentUser: User? = contexts[request]["user"]
    val users = database.sequenceOf(Users).toList()

    val pending = if (currentUser!=null) {
        database.sequenceOf(FriendRequests)
            .filter { it.receiverId eq currentUser.id }
            .toList()
    } else {
        null
    }

    val query = database
        .from(Users)
        .leftJoin(FriendRequests, on = Users.id eq FriendRequests.receiverId )
        .select(Users.firstName, Users.lastName, Users.username)

    println("our query here $query")

    val viewModel = ListUsersViewModel(users, pending)
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

