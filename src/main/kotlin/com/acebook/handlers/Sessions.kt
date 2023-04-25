package com.acebook.handlers

import com.acebook.*
import com.acebook.entities.User
import com.acebook.schemas.Users
import com.acebook.viewmodels.LoginViewModel
import org.http4k.core.*
import org.http4k.core.cookie.Cookie
import org.http4k.core.cookie.cookie
import org.http4k.core.cookie.removeCookie
import org.ktorm.dsl.eq
import org.ktorm.entity.filter
import org.ktorm.entity.first
import org.ktorm.entity.sequenceOf
import org.mindrot.jbcrypt.BCrypt
import java.util.*

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