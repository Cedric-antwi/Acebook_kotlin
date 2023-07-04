package com.acebook.handlers

import com.acebook.database
import com.acebook.entities.User
import com.acebook.schemas.Comments
import com.acebook.schemas.Posts
import com.acebook.templateRenderer
import com.acebook.viewmodels.CommentViewModel
import com.acebook.viewmodels.FeedViewModel
import org.http4k.core.*
import org.ktorm.dsl.eq
import org.ktorm.entity.filter
import org.ktorm.entity.sequenceOf
import org.ktorm.entity.toList

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