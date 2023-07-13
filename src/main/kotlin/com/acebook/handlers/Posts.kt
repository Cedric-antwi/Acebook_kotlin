package com.acebook.handlers

import com.acebook.*
import com.acebook.entities.Like
import com.acebook.entities.Post
import com.acebook.entities.User
import com.acebook.schemas.FriendRequests
import com.acebook.schemas.Likes
import com.acebook.schemas.Posts
import com.acebook.schemas.Users
import com.acebook.viewmodels.FeedViewModel
import com.acebook.viewmodels.FriendRequestViewModel
import com.acebook.viewmodels.PostViewModel
import org.http4k.core.*
import org.ktorm.dsl.*
import org.ktorm.entity.*
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

fun indexHandler(contexts: RequestContexts): HttpHandler = { request: Request ->
    val posts = database.sequenceOf(Posts).sortedBy { it.dateCreated }.toList().reversed()
    val currentUser: User? = contexts[request]["user"]
    val myFriends = mutableListOf<FriendRequestViewModel>()
    if (currentUser != null) {
        for (row in database
            .from(FriendRequests)
            .innerJoin(Users, on = Users.id eq FriendRequests.senderId)
            .select( FriendRequests.id, Users.id, Users.firstName, Users.lastName, Users.username, Users.image)
            .where {
                (FriendRequests.friendshipStatus eq true)and
                (currentUser.id eq FriendRequests.receiverId)or
                (currentUser.id eq FriendRequests.senderId)
            }
        ) {
            myFriends.add(FriendRequestViewModel (
                row[Users.id]!!,
                row[FriendRequests.id]!!,
                row[Users.firstName].toString(),
                row[Users.lastName].toString(),
                row[Users.username].toString(),
                row[Users.image].toString()
            ))
        }
    }

    val viewModel = FeedViewModel(posts, currentUser, myFriends)

    Response(Status.OK)
        .body(templateRenderer(viewModel))
}

fun newPostHandler(contexts: RequestContexts): HttpHandler = { request: Request ->
    val currentUser: User? = contexts[request]["user"]
    val viewModel = PostViewModel("",  currentUser = currentUser)
    Response(Status.OK)
        .body(templateRenderer(viewModel))
}

fun createNewPost(contexts: RequestContexts): HttpHandler = { request: Request ->
    val receivedForm = MultipartFormBody.from(request)
    val pictureFile = receivedForm.file("picture")
    val text = receivedForm.fieldValue("text")
    val currentTime = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val formattedDateTime = currentTime.format(formatter)

    if (pictureFile != null && text != null) {
        // Handle picture upload
        val pictureFilename = pictureFile.filename ?: ""
        val contentType = pictureFile.contentType ?: ""
        val inputStream = pictureFile.content

        // Generate a unique filename using UUID
        val uniqueFilename = UUID.randomUUID().toString()
        val extension = pictureFilename.substringAfterLast(".", "")
        val savedFilename = "$uniqueFilename.$extension"

        // Specify the directory where the pictures will be saved
        val uploadDirectory = "/Users/cau4611/Desktop/MakersCode/acebook-kotlin-http4k-template/src/main/resources/static"


        // Save the picture to the upload directory
        val savedFile = File(uploadDirectory, savedFilename)
        inputStream.use { input ->
            savedFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        val fileSize = savedFile.readBytes().size

        val pictureLink = "/static/$savedFilename"
        val currentUser: User? = contexts[request]["user"]
        val userPost = Post {
            content = text
            dateCreated = formattedDateTime
            if (currentUser != null) {
                userId = currentUser.id
                authorName = currentUser.username
                authorImage = currentUser.image
            }

            postImage = if(fileSize == 0){
                null
            } else{
                pictureLink
            }
        }

        database.sequenceOf(Posts).add(userPost)

        Response(Status.SEE_OTHER)
            .header("Location", "/")
            .body("")
    } else {
        Response(Status.BAD_REQUEST).body("Incomplete form data")
    }
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
