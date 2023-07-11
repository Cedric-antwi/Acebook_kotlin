package com.acebook

import com.natpryce.hamkrest.assertion.assertThat
import okhttp3.OkHttpClient
import org.http4k.client.OkHttp
import org.http4k.core.*
import org.http4k.hamkrest.hasStatus
import org.http4k.lens.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.riversun.okhttp3.OkHttp3CookieHelper

class CreatePostTest {

    @BeforeEach
    fun setup() {
    }

    @Test
    fun `Cannot create new post if not signed in`() {
        val client = OkHttp()
        val response: Response = client(Request(Method.GET, "http://localhost:9999/posts/new"))
        assert(response.status == Status.FOUND)
        assert(response.header("Location") == "/sessions/new")
    }

    @Test

    fun `Signup and Login and creating a Post return 200 OK response`() {
        val cookieHelper = OkHttp3CookieHelper()
        val client = OkHttp(OkHttpClient().newBuilder().cookieJar(cookieHelper.cookieJar()).build())
        val form = WebForm(
            mapOf(
                "email" to listOf("email"),
                "password" to listOf("password"),
                "firstname" to listOf("firstname"),
                "lastname" to listOf("firstname"),
                "username" to listOf("firstname")
            ))
        val response: Response = client(
            Request(Method.POST, "http://localhost:9999/users").with(
                requiredSignupFormLens of form
            )
                .header("content-type", "application/x-www-form-urlencoded")
        )
        assertThat(response, hasStatus(Status.OK))

        val form2 = WebForm(
            mapOf(
                "email" to listOf("email"),
                "password" to listOf("password")
            ))

        val response2: Response = client(
            Request(Method.POST, "http://localhost:9999/sessions").with(
                requiredLoginCredentialsLens of form2
            )
                .header("content-type", "application/x-www-form-urlencoded")
        )

        assertThat(response2, hasStatus(Status.OK))
        val nameField = MultipartFormField.string().required("text")
        val imageFile = MultipartFormFile.optional("picture")
        val strictFormBody = Body.multipartForm(Validator.Strict, nameField,imageFile).toLens()
        val multipartform = MultipartForm().with(
            nameField of ("Hello there").toString(),
            imageFile of MultipartFormFile(
                "",
                ContentType.OCTET_STREAM,
                "somebinarycontent".byteInputStream()
            )
        )

        val response3: Response = client(
            Request(Method.POST, "http://localhost:9999/posts").with(
                strictFormBody of multipartform
            )
                .header("content-type", "application/x-www-form-urlencoded")
        )
        assert(response3.bodyString().contains("Welcome"))
    }
}