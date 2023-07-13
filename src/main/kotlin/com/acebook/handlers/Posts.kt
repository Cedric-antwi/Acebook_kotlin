package com.acebook.handlers

import com.acebook.*
import com.acebook.entities.Like
import com.acebook.entities.Post
import com.acebook.entities.User
import com.acebook.schemas.Likes
import com.acebook.schemas.Posts
import com.acebook.viewmodels.FeedViewModel
import com.acebook.viewmodels.PostViewModel
import org.http4k.core.*
import org.ktorm.dsl.*
import org.ktorm.entity.*
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

data class APost(val Id:Int?, val content: String?, val userId: Int?, val dateCreated: String?, val authorName: String?,val authorImage: String?,val likesCount:Int?,val postImage:String?, val delete: Boolean? =false)

fun indexHandler(contexts: RequestContexts): HttpHandler = { request: Request ->
//    val allPosts: List<Post>
    val currentUser: User? = contexts[request]["user"]
    val posts: List<APost> = database.from(Posts).select()
        .map {
            if (currentUser != null) {
                APost(
                    it[Posts.id],
                    it[Posts.content],
                    it[Posts.userId],
                    it[Posts.dateCreated],
                    it[Posts.authorName],
                    it[Posts.authorImage],
                    it[Posts.likesCount],
                    it[Posts.postImage],
                    it[Posts.userId] == currentUser.id
                )
            } else {
                APost(
                    it[Posts.id],
                    it[Posts.content],
                    it[Posts.userId],
                    it[Posts.dateCreated],
                    it[Posts.authorName],
                    it[Posts.authorImage],
                    it[Posts.likesCount],
                    it[Posts.postImage],
                    false
                )
            }
        }

    println(posts)

    val viewModel = FeedViewModel(posts.sortedBy { it.dateCreated }.toList().reversed(), currentUser)

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
        val uploadDirectory = "/Users/mmu4265/KotlinStuff/ace_kotlin/src/main/resources/static"


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
    else {
        database.update(Posts){
            set(it.likesCount, (currentLikes -1))
            where{
                it.id eq id
            }
        }
        database.delete(Likes) {
            if (currentUser != null) {
                it.userId eq currentUser.id
            }
            it.postId eq id
        }
    }
    return Response(Status.SEE_OTHER)
        .header("Location", "/")
        .body("")
}
fun deletePost(request: Request, id:Int): Response{
    database.delete(Posts)
    {it.id eq id}
    return Response(Status.SEE_OTHER)
        .header("Location", "/")
        .body("")

}
fun editPost(contexts: RequestContexts, request: Request, id: Int): Response {
    val currentUser: User? = contexts[request]["user"]
    val post = database.sequenceOf(Posts).firstOrNull { it.id eq id }

    if (post != null) {
        val receivedForm = MultipartFormBody.from(request)
        val pictureFile = receivedForm.file("picture")
        val text = receivedForm.fieldValue("text")

        if (pictureFile != null && text != null) {
            // Handle picture upload and update post with new content and image
            val pictureFilename = pictureFile.filename ?: ""
            val contentType = pictureFile.contentType ?: ""
            val inputStream = pictureFile.content

            // Generate a unique filename using UUID
            val uniqueFilename = UUID.randomUUID().toString()
            val extension = pictureFilename.substringAfterLast(".", "")
            val savedFilename = "$uniqueFilename.$extension"

            // Specify the directory where the pictures will be saved
            val uploadDirectory = "/Users/mmu4265/KotlinStuff/ace_kotlin/src/main/resources/static"

            // Save the picture to the upload directory
            val savedFile = File(uploadDirectory, savedFilename)
            inputStream.use { input ->
                savedFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            val fileSize = savedFile.readBytes().size
            val pictureLink = "/static/$savedFilename"

            database.update(Posts) {
                set(it.content, text)
                set(it.postImage, if (fileSize == 0) null else pictureLink)
                where { it.id eq id }
            }
        } else if (text != null) {
            // Update post content only
            database.update(Posts) {
                set(it.content, text)
                where { it.id eq id }
            }
        }

        return Response(Status.SEE_OTHER)
            .header("Location", "/")
            .body("")
    }

    return Response(Status.BAD_REQUEST).body("Invalid post")
}