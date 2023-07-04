package com.acebook.handlers

import com.acebook.*
import com.acebook.entities.Comment
import com.acebook.entities.Post
import com.acebook.entities.User
import com.acebook.requiredContentLens
import com.acebook.schemas.Comments
import com.acebook.schemas.Posts
import com.acebook.viewmodels.CommentViewModel
import com.acebook.viewmodels.FeedViewModel
import org.http4k.core.*
import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.filter
import org.ktorm.entity.sequenceOf
import org.ktorm.entity.toList
import java.time.LocalDateTime

fun viewAllComments(contexts: RequestContexts, request: Request, id: Int): Response {
    val getAllComments = database.sequenceOf(Comments)
        .filter { it.postId eq id }
        .toList()
    println("COMMENTS")
    println(getAllComments)

    val getCurrentPost = database.sequenceOf(Posts)
        .filter { it.id eq id }
        .toList()

    println("POSTS")
    println(getCurrentPost)

    val post = getCurrentPost[0]
    val currentUser: User? = contexts[request]["user"]

    val viewModel = CommentViewModel(getAllComments, post, currentUser = currentUser)
    val render = templateRenderer(viewModel)
    return Response(Status.OK)
        .body(render)
}
//fun addNewcomment()
fun addNewcomment(contexts: RequestContexts, request: Request, id: Int): Response {
    val form = requiredCommentFormLens (request)
    val newPost = requiredCommentContent (form)
    val currentUser: User? = contexts[request]["user"]
    val currentTime = LocalDateTime.now()
    val userComment = Comment {
        commentBody = newPost
        dateCreated = currentTime
        postId = id
        if (currentUser != null) {
            userId = currentUser.id
        }
    }
    database.sequenceOf(Comments).add(userComment)

    return Response(Status.SEE_OTHER)
        .header("Location", "/posts/$id")
        .body("")
}