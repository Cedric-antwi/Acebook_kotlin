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
import org.ktorm.entity.add
import org.ktorm.entity.sequenceOf
import org.ktorm.entity.toList


fun indexHandler(contexts: RequestContexts): HttpHandler = { request: Request ->
    val posts = database.sequenceOf(Posts).toList().reversed()
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

fun createNewPost(): HttpHandler = {request: Request ->
    val form = requiredContentLens (request)
    val newPost = requiredPostContent(form)

    val userPost = Post {
        content = newPost
    }
    val post = database.sequenceOf(Posts).add(userPost)

    Response(Status.OK)
}