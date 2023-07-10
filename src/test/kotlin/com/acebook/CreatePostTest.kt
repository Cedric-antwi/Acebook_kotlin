package com.acebook

import com.acebook.requiredLoginCredentialsLens
import com.acebook.requiredSignupFormLens
import com.acebook.schemas.Posts
import com.acebook.schemas.Users
import com.natpryce.hamkrest.assertion.assertThat
import okhttp3.OkHttpClient
import org.http4k.client.OkHttp
import org.http4k.core.*
import org.http4k.hamkrest.hasStatus
import org.http4k.lens.MultipartForm
import org.http4k.lens.Validator
import org.http4k.lens.WebForm
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.ktorm.database.Database
import org.ktorm.dsl.deleteAll
import org.riversun.okhttp3.OkHttp3CookieHelper
import org.http4k.lens.multipartForm

class CreatePostTest {

    @BeforeEach
    fun setup() {
//        database.deleteAll(Posts)
//        database.deleteAll(Users)
    }

    @Test
    fun `Cannot create new post if not signed in`() {
        val client = OkHttp()
        val response: Response = client(Request(Method.GET, "http://localhost:9999/posts/new"))

        assert(response.status == Status.FOUND)
        assert(response.header("Location") == "/sessions/new")
    }

    @Test

//    fun `Signup and Login and creating a Post return 200 OK response`() {
//        val cookieHelper = OkHttp3CookieHelper()
//        val client = OkHttp(OkHttpClient().newBuilder().cookieJar(cookieHelper.cookieJar()).build())
//        val form = WebForm(
//            mapOf(
//                "email" to listOf("test@acebook.com"),
//                "password" to listOf("s3cr3tp4ss"),
//                "firstname" to listOf("Tester"),
//                "lastname" to listOf("User"),
//                "username" to listOf("Tester_User")
//            ))
//
//        val response: Response = client(
//            Request(Method.POST, "http://localhost:9999/users").with(
//                requiredSignupFormLens of form
//            )
//                .header("content-type", "application/x-www-form-urlencoded")
//        )
//
//        assertThat(response, hasStatus(Status.OK))
//        assert(response.bodyString().contains("Login"))
//        val form2 = WebForm(
//            mapOf(
//                "email" to listOf("email"),
//                "password" to listOf("password")
//            ))
//
//        val response2: Response = client(
//            Request(Method.POST, "http://localhost:9999/sessions").with(
//                requiredLoginCredentialsLens of form2
//            )
//                .header("content-type", "application/x-www-form-urlencoded")
//        )
//
//        assertThat(response2, hasStatus(Status.OK))
//        assert(response2.bodyString().contains("Welcome"))
//
//
//
//        val form3 = WebForm(
//            mapOf(
//                "content" to listOf("content")
//            )
//        )
//
//        val response3: Response = client(
//            Request(Method.POST, "http://localhost:9999/posts").with(
//                MultipartForm()
//            )
//                .header("multipart/form-data", "application/x-www-form-urlencoded")
//        )
//
//        assert(response3.bodyString().contains("Welcome"))
//
//    }
//}