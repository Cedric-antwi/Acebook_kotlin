package com.acebook.handlers

import com.acebook.*
import com.acebook.entities.Like
import com.acebook.entities.Post
import com.acebook.entities.User
import com.acebook.requiredPostContent
import com.acebook.schemas.Likes
import com.acebook.schemas.Posts
import com.acebook.viewmodels.FeedViewModel
import com.acebook.viewmodels.PostViewModel
import org.http4k.core.*
import org.http4k.lens.WebForm
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.dsl.update
import org.ktorm.entity.*
import java.time.LocalDateTime


fun indexHandler(contexts: RequestContexts): HttpHandler = { request: Request ->
    val posts = database.sequenceOf(Posts).sortedBy { it.dateCreated }.toList().reversed()
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
            authorName = currentUser.username
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
    val currentUser: User? = contexts[request]["user"]
    val currentLikes = post.likesCount
    val getLikes = if (currentUser!=null) {
        database.sequenceOf(Likes)
            .filter { (it.postId eq post.id) and (it.userId eq currentUser.id) }
            .firstOrNull()
    }else{
        null
    }
    if(getLikes == null){
        database.update(Posts)
        {
            set(it.likesCount, (currentLikes + 1))
            where {
                it.id eq id
            }
        }
        val newLike =Like{
            if (currentUser != null) {
                userId = currentUser.id
            }
            postId =post.id
        }

        database.sequenceOf(Likes).add(newLike)
    }
    return Response(Status.SEE_OTHER)
        .header("Location", "/")
        .body("")
}
