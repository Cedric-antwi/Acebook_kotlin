package com.acebook.handlers

import com.acebook.*
import com.acebook.entities.Post
import com.acebook.entities.User
import com.acebook.requiredPostContent
import com.acebook.schemas.Posts
import com.acebook.viewmodels.FeedViewModel
import com.acebook.viewmodels.PostViewModel
import org.http4k.core.*
import org.http4k.lens.WebForm
import org.ktorm.dsl.eq
import org.ktorm.dsl.update
import org.ktorm.entity.add
import org.ktorm.entity.filter
import org.ktorm.entity.sequenceOf
import org.ktorm.entity.toList
import java.time.LocalDateTime


fun indexHandler(contexts: RequestContexts): HttpHandler = { request: Request ->
    val posts = database.sequenceOf(Posts).toList().reversed()
    println(posts)
    val currentUser: User? = contexts[request]["user"]
    val viewModel = FeedViewModel(posts, currentUser)

    Response(Status.OK)
        .body(templateRenderer(viewModel))
}

fun newPostHandler(contexts: RequestContexts): HttpHandler = { request: Request ->
    val currentUser: User? = contexts[request]["user"]
    val viewModel = PostViewModel("",  currentUser = currentUser)

    Response(Status.OK)
        .body(templateRenderer(viewModel))
}

fun createNewPost(contexts: RequestContexts): HttpHandler = {request: Request ->
    val form = requiredContentLens (request)
    val newPost = requiredPostContent(form)
    val currentUser: User? = contexts[request]["user"]
    val currentTime = LocalDateTime.now()
    val userPost = Post {
        content = newPost
        dateCreated = currentTime
        if (currentUser != null) {
            userId = currentUser.id
        }
        if (currentUser != null) {
            authorName = currentUser.email
        }


    }
    val post = database.sequenceOf(Posts).add(userPost)

    Response(Status.SEE_OTHER)
        .header("Location", "/")
        .body("")
}

fun likePost(contexts: RequestContexts, request: Request, id: Int): Response {
    val getCurrentPost = database.sequenceOf(Posts)
        .filter { it.id eq id }
        .toList()
    val post = getCurrentPost[0]
    val currentLikes = post.likesCount
    database.update(Posts){
        set(it.likesCount, (currentLikes+1))
        where {
            it.id eq id
        }
    }

    return Response(Status.SEE_OTHER)
        .header("Location", "/")
        .body("")
}