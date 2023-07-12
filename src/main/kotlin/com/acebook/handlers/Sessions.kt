package com.acebook.handlers

import com.acebook.*
import com.acebook.entities.User
import com.acebook.schemas.Posts
import com.acebook.schemas.Users
import com.acebook.viewmodels.LoginViewModel
import com.acebook.viewmodels.ProfileSettingsViewModel
import org.http4k.core.*
import org.http4k.core.cookie.Cookie
import org.http4k.core.cookie.cookie
import org.http4k.core.cookie.removeCookie
import org.ktorm.dsl.eq
import org.ktorm.dsl.update
import org.mindrot.jbcrypt.BCrypt
import java.io.File
import java.util.*
import org.http4k.core.MultipartFormBody
import org.http4k.core.Request
import org.http4k.core.Response
import org.ktorm.entity.*
import java.util.UUID

fun newSessionHandler(): HttpHandler = {
    val viewModel = LoginViewModel("", "")
    Response(Status.OK)
        .body(templateRenderer(viewModel))
}

fun createSessionHandler(): HttpHandler = { request: Request ->
    val form = requiredLoginCredentialsLens.extract(request)
    val email = requiredEmailField(form)
    val password = requiredPasswordField(form)
    val user = authenticateUser(email, password)
    if (user == null) {
        Response(Status.FOUND).header("Location", "/sessions/new")
    } else {
        injectSessionCookie(
            Response(Status.FOUND).header("Location", "/"),
            user
        )
    }
}

fun destroySessionHandler(): HttpHandler = {
    val cookie = it.cookie("acebook_session_id")
    val sessionId = cookie?.value
    if (sessionId != null) {
        sessionCache.invalidate(sessionId)
    }

    Response(Status.FOUND)
        .header("Location", "/")
        .removeCookie("acebook_session_id")
}

private fun authenticateUser(email: String, password: String): User? {
    try {
        val user = database.sequenceOf(Users)
            .filter { it.email eq email }
            .first()

        if (BCrypt.checkpw(password, user.encryptedPassword)) {
            return user
        } else {
            return null
        }
    } catch (e: NoSuchElementException) {
        return null
    }
}

private fun injectSessionCookie(response: Response, user: User): Response {
    val sessionId = UUID.randomUUID().toString()
    sessionCache.put(sessionId, user.id)
    return response.cookie(Cookie("acebook_session_id", sessionId))
}

fun viewProfile(contexts: RequestContexts):  HttpHandler = { request: Request ->
    val currentUser: User? = contexts[request]["user"]
    val viewModel = ProfileSettingsViewModel(currentUser = currentUser)
    Response(Status.OK)
        .body(templateRenderer(viewModel))
}

fun updateProfile(contexts: RequestContexts):  HttpHandler = { request: Request ->
    val receivedForm = MultipartFormBody.from(request)
    val file = receivedForm.file("picture")
    val filename = file?.filename
    val contentType = file?.contentType
    val inputStream = file?.content
    if (filename != null && contentType != null && inputStream != null) {
        // Generate a unique filename using UUID
        val uniqueFilename = UUID.randomUUID().toString()
        val extension = filename.substringAfterLast(".", "")
        val savedFilename = "$uniqueFilename.$extension"

        // Specify the directory where the pictures will be saved
        val uploadDirectory = "/Users/mou4587/Acebook_kotlin/src/main/resources/static"

        // Save the picture to the upload directory
        val savedFile = File(uploadDirectory, savedFilename)
        inputStream.use { input ->
            savedFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        val pictureLink = "/static/$savedFilename"
        val currentUser: User? = contexts[request]["user"]
        database.update(Users) {
            set(it.image, pictureLink)
            if (currentUser != null) {
                where {
                    it.id eq currentUser.id
                }
            }
        }

        Response(Status.SEE_OTHER)
            .header("Location", "/settings/editprofile")
            .body("")

    } else {
        Response(Status.BAD_REQUEST).body("No picture uploaded")
    }
}

fun editInfo(contexts: RequestContexts, request: Request, id: Int): Response {
    val form = requiredEditProfileLens(request)
    val inputUsername = requiredUsernameField(form)
    val inputFirstname = requiredFirstnameField(form)
    val inputLastname = requiredLastnameField(form)
    val getCurrentUser = database.sequenceOf(Users)
        .filter { it.id eq id }
        .toList()
    val user = getCurrentUser[0]
    println("HERE $user")

    database.update(Users) {
        set(it.username, inputUsername)
        set(it.firstName, inputFirstname)
        set(it.lastName, inputLastname)
        where {
            it.id eq user.id
        }
    }

    database.update(Posts) {
        set(it.authorName, inputUsername)
        where {
            it.userId eq user.id
        }
    }

    return Response(Status.SEE_OTHER)
        .header("Location", "/settings/editprofile")
        .body("")
}
