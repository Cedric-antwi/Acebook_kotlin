package com.acebook

import com.acebook.handlers.*
import com.acebook.schemas.Users
import org.http4k.core.*
import org.http4k.core.cookie.cookie
import org.http4k.filter.DebuggingFilters
import org.http4k.filter.ServerFilters
import org.http4k.lens.*
import org.http4k.routing.ResourceLoader
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.routing.static
import org.ktorm.dsl.eq
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf

val requiredEmailField = FormField.nonEmptyString().required("email")
val requiredPasswordField = FormField.nonEmptyString().required("password")
val requiredUsernameField = FormField.nonEmptyString().required("username")
val requiredFirstnameField = FormField.nonEmptyString().required("firstname")
val requiredLastnameField = FormField.nonEmptyString().required("lastname")

val requiredLoginCredentialsLens = Body.webForm(
    Validator.Strict,
    requiredEmailField,
    requiredPasswordField
).toLens()
val requiredSignupFormLens = Body.webForm(
    Validator.Strict,
    requiredEmailField,
    requiredPasswordField,
    requiredFirstnameField,
    requiredLastnameField
).toLens()

val requiredEditProfileLens = Body.webForm(
    Validator.Strict,
    requiredUsernameField,
    requiredFirstnameField,
    requiredLastnameField
).toLens()



val requiredPostContent = FormField.nonEmptyString().required("content")

val requiredContentLens = Body.webForm(
    Validator.Strict,
    requiredPostContent
).toLens()

val requiredPictureField= FormField.required("picture")

val requiredProfileFormLens = Body.webForm(
    Validator.Strict,
    requiredPictureField
).toLens()


val requiredCommentContent = FormField.nonEmptyString().required("comment")
val requiredCommentFormLens = Body.webForm(
    Validator.Strict,
    requiredCommentContent
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
    "/like/{id}" bind Method.POST to{request: Request ->
        val idParamLens = Path.int().of ( "id")
        val id =idParamLens(request)
        likePost(contexts, request, id )
    },

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
        "/new" bind Method.GET to checkAuthenticated(contexts).then(newPostHandler(contexts)),
        "/" bind Method.POST to createNewPost(contexts = contexts),
        "/{id}" bind Method.GET to{request: Request ->
            val idParamLens = Path.int().of ( "id")
            val id =idParamLens(request)
            viewAllComments(contexts, request, id )
            },
        "/add/{id}" bind Method.POST to{request: Request ->
            val idParamLens = Path.int().of ( "id")
            val id =idParamLens(request)
            addNewcomment(contexts, request, id )
        },
        "/delete-post/{id}" bind Method.GET to {request: Request->
            val idParamLens = Path.int().of ( "id")
            val id =idParamLens(request)
            deletePost(request,id)
        },
        "/edit-post/{id}" bind Method.POST to {request: Request->
            val idParamLens = Path.int().of ( "id")
            val id =idParamLens(request)
            editPost(contexts,request,id)
        },
    ),
   "/comment-like" bind routes(
       "/{id}" bind Method.POST to{request: Request ->
           val idParamLens = Path.int().of ( "id")
           val id =idParamLens(request)
           likeComments(contexts, request, id )
       },
    ),

    "/settings" bind routes(
        "/editprofile" bind Method.GET to viewProfile(contexts),
        "/" bind Method.POST to updateProfile(contexts),
        "/editinfo/{id}" bind Method.POST to { request: Request ->
            val idParamLens = Path.int().of("id")
            val id = idParamLens(request)
            editInfo(contexts, request, id)
        }
    ),

    "/friendslist" bind routes(
        "/request" bind Method.GET to listUsers(contexts),
        "/pending-friend/{id}" bind Method.GET to { request: Request ->
            val idParamLens = Path.int().of("id")
            val friendId = idParamLens(request)
            friendRequest(contexts, request, friendId)
        },
        "/accept/{id}" bind Method.GET to { request: Request ->
            val idParamLens = Path.int().of("id")
            val friendId = idParamLens(request)
            acceptReq(friendId ,contexts, request)
        },
        "/delete/{id}" bind Method.GET to { request: Request ->
            val idParamLens = Path.int().of("id")
            val friendId = idParamLens(request)
            deleteReq(friendId, contexts, request)
        }
    ),

    "/static" bind static(ResourceLoader.Directory("src/main/resources/static"))
)

fun failResponse (failure: LensFailure) =
    Response(Status.BAD_REQUEST).body("Invalid parameters 1")

fun appHttpHandler(contexts: RequestContexts): HttpHandler =
    ServerFilters.InitialiseRequestContext(contexts)
        .then(ServerFilters.CatchLensFailure(::failResponse))
        .then(DebuggingFilters.PrintRequest())
        .then(authenticateRequestFromSession(contexts))
        .then(app(contexts))