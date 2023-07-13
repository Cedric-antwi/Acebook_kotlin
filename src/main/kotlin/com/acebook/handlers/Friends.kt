package com.acebook.handlers

import com.acebook.*
import com.acebook.entities.Comment.Companion.invoke
import com.acebook.entities.FriendRequest
import com.acebook.entities.User
import com.acebook.schemas.FriendRequests
import com.acebook.schemas.Posts
import com.acebook.schemas.Users
import com.acebook.templateRenderer
import com.acebook.viewmodels.FriendRequestViewModel
import com.acebook.viewmodels.ListUsersViewModel
import org.http4k.core.*
import org.ktorm.entity.add
import org.ktorm.entity.filter
import org.ktorm.entity.sequenceOf
import org.ktorm.entity.toList
import org.ktorm.database.*
import org.ktorm.dsl.*
import org.ktorm.dsl.Query

//import io.reactivex.Completable

fun queryHandleBar(contexts: RequestContexts, request: Request): MutableList<FriendRequestViewModel> {
    val currentUser: User? = contexts[request]["user"]
    var query = mutableListOf<FriendRequestViewModel>()
    if (currentUser != null) {

        for (row in database
            .from(FriendRequests)
            .innerJoin(Users, on = Users.id eq FriendRequests.senderId)
            .select( FriendRequests.id,Users.id, Users.firstName, Users.lastName, Users.username, Users.image)
            .where {
                (currentUser.id eq FriendRequests.receiverId)and
                (FriendRequests.requestStatus eq false)and
                (FriendRequests.friendshipStatus eq false)
            }
        )
        {
            query.add(FriendRequestViewModel (
                row[Users.id]!!,
                row[FriendRequests.id]!!,
                row[Users.firstName].toString(),
                row[Users.lastName].toString(),
                row[Users.username].toString(),
                row[Users.image].toString(),
                row[FriendRequests.friendshipStatus]
            ))
        }
    } else {
        query = mutableListOf<FriendRequestViewModel>()
        return query
    }
    return query
}
fun listUsers(contexts: RequestContexts): HttpHandler = { request: Request ->
    val currentUser: User? = contexts[request]["user"]
    //instantiating a mutable list of all the users
    val allUsersList: MutableList<User> = database.sequenceOf(Users).toList().toMutableList()
    //filtering through the allUsersList to exclude current user
    val filteredList = allUsersList.filter { user -> user.id != currentUser?.id }.toMutableList()

    val pendingReq = queryHandleBar(contexts, request)
    val viewModel = ListUsersViewModel(users = filteredList, pendingReq, currentUser = currentUser)
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
        friendshipStatus = false
    }

    database.sequenceOf(com.acebook.schemas.FriendRequests).add(newFriendRequest)
    return Response(Status.SEE_OTHER)
        .header("Location", "/friendslist/request")
        .body("")
}

fun acceptReq(friendId: Int, contexts: RequestContexts, request: Request): Response {
    val currentUser: User? = contexts[request]["user"]
    database.update(FriendRequests)
    {
        set(
            it.requestStatus, true
        )
        set(
            it.friendshipStatus, true
        )
        if (currentUser != null) {
            where {
                (it.receiverId eq currentUser.id)and
                (it.senderId eq friendId)
            }
        }

    }



    return Response(Status.SEE_OTHER)
        .header("Location", "/friendslist/request")
        .body("")
}

fun deleteReq(friendId: Int, contexts: RequestContexts, request: Request): Response {
    val currentUser: User? = contexts[request]["user"]
    database.delete(FriendRequests)
    {
        it.id eq friendId
    }
    return Response(Status.SEE_OTHER)
        .header("Location", "/friendslist/request")
        .body("")
}