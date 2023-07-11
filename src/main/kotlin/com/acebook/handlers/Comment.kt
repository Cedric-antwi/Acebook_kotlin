package com.acebook.handlers

import com.acebook.*
import com.acebook.entities.Comment
import com.acebook.entities.Like
import com.acebook.entities.User
import com.acebook.schemas.Comments
import com.acebook.schemas.Likes
import com.acebook.schemas.Posts
import com.acebook.viewmodels.CommentViewModel
import org.http4k.core.*
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.dsl.update
import org.ktorm.entity.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun viewAllComments(contexts: RequestContexts, request: Request, id: Int): Response {
    val getAllComments = database.sequenceOf(Comments)
        .filter { it.postId eq id }
        .sortedBy { it.dateCreated }.toList().reversed()
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

fun addNewcomment(contexts: RequestContexts, request: Request, id: Int): Response {
    val form = requiredCommentFormLens (request)
    val newPost = requiredCommentContent (form)
    val currentUser: User? = contexts[request]["user"]
    val currentTime = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val formattedDateTime = currentTime.format(formatter)
    val userComment = Comment {
            commentBody = newPost
            dateCreated = formattedDateTime
            postId = id
            if (currentUser != null) {
                userId = currentUser.id
                authorName = currentUser.username
                authorImage=currentUser.image
            }

        }
    database.sequenceOf(Comments).add(userComment)

    return Response(Status.SEE_OTHER)
        .header("Location", "/posts/$id")
        .body("")
}

// Function for counting the likes on the comments
fun likeComments(contexts: RequestContexts, request: Request, id: Int): Response {
    val getCurrentComment = database.sequenceOf(Comments)
        .filter { it.id eq id }
        .toList()
    val comment = getCurrentComment[0]
    val currentUser: User? = contexts[request]["user"]
    val currentLikes = comment.commentsLikeCount
    val getLikesOnComments = if (currentUser!=null) {
        database.sequenceOf(Likes)
            .filter { (it.commentId eq comment.id) and (it.userId eq currentUser.id) and (it.postId eq comment.postId) }
            .firstOrNull()
    }else{
        null
    }

    if(getLikesOnComments == null){
        database.update(Comments)
        {
            set(it.commentsLikeCount, (currentLikes + 1))
            where {
                it.id eq id
            }
        }
        val newLike =Like{
            if (currentUser != null) {
                userId = currentUser.id
            }
            commentId =comment.id
            postId = comment.postId
        }
        database.sequenceOf(Likes).add(newLike)
    }

    return currentUser?.let {
        Response(Status.SEE_OTHER)
            .header("Location", "/posts/${comment.postId}")
            .body("")
    } ?: Response(Status.SEE_OTHER)
        .header("Location", "/")
        .body("")
}