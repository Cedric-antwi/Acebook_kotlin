package com.acebook

import com.acebook.handlers.*
import com.acebook.schemas.Users
import io.github.reactivecircus.cache4k.Cache
import org.http4k.core.*
import org.http4k.core.cookie.cookie
import org.http4k.filter.DebuggingFilters
import org.http4k.filter.ServerFilters
import org.http4k.lens.*
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.template.HandlebarsTemplates
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf

// https://www.http4k.org/guide/concepts/lens/

val requiredEmailField = FormField.nonEmptyString().required("email")
val requiredPasswordField = FormField.nonEmptyString().required("password")
val requiredLoginCredentialsLens = Body.webForm(
    Validator.Strict,
    requiredEmailField,
    requiredPasswordField
).toLens()
val requiredSignupFormLens = Body.webForm(
    Validator.Strict,
    requiredEmailField,
    requiredPasswordField
).toLens()

fun checkAuthenticated(contexts: RequestContexts) = Filter { next ->
    {
        if (contexts[it].get<Int>("user_id") == null) {
            Response(Status.FOUND).header("Location", "/sessions/new")
        } else {
            next(it)
        }
    }
}

fun authenticateRequestFromSession(contexts: RequestContexts) = Filter { next ->
    {
        val cookie = it.cookie("acebook_session_id")
        val sessionId = cookie?.value

        if (sessionId != null) {
            val userId = sessionCache.get(sessionId) ?: null
            if (userId != null) {
                val currentUser = database.sequenceOf(Users).find { it.id eq userId }

                contexts[it]["user_id"] = userId
                contexts[it]["user"] = currentUser
            }
        }

        next(it)
    }
}

fun app(contexts: RequestContexts) = routes(

    "/" bind Method.GET to indexHandler(contexts),

    "/users" bind routes(
        "/new" bind Method.GET to newUserHandler(),
        "/" bind Method.POST to createUserHandler()
    ),

    "/sessions" bind routes(
        "/new" bind Method.GET to newSessionHandler(),
        "/" bind Method.POST to createSessionHandler(),
        "/clear" bind Method.GET to destroySessionHandler()
    ),

    "/posts" bind routes(
        "/new" bind Method.GET to checkAuthenticated(contexts).then(newPostHandler(contexts))

        // TODO: Implement the route to create a new post
    )
)

fun failResponse (failure: LensFailure) =
    Response(Status.BAD_REQUEST).body("Invalid parameters")

fun appHttpHandler(contexts: RequestContexts): HttpHandler =
    ServerFilters.InitialiseRequestContext(contexts)
        .then(ServerFilters.CatchLensFailure(::failResponse))
        .then(DebuggingFilters.PrintRequest())
        .then(authenticateRequestFromSession(contexts))
        .then(app(contexts))