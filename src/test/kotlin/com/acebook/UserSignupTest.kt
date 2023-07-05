package com.acebook

import com.acebook.requiredLoginCredentialsLens
import com.acebook.requiredSignupFormLens
import com.acebook.schemas.Users
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.containsSubstring
import okhttp3.OkHttpClient
import org.http4k.client.OkHttp
import org.http4k.core.*
import org.http4k.hamkrest.*
import org.http4k.lens.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.ktorm.database.Database
import org.ktorm.dsl.deleteAll
import org.riversun.okhttp3.OkHttp3CookieHelper

class UserSignupTest {
    val requiredEmailField = FormField.nonEmptyString().required("email")
    val requiredPasswordField = FormField.nonEmptyString().required("password")
    val requiredPostContent = FormField.nonEmptyString().required("content")
    val requiredSignupFormLens = Body.webForm(
        Validator.Strict,
        requiredEmailField,
        requiredPasswordField
    ).toLens()

    val requiredLoginCredentialsLens = Body.webForm(
        Validator.Strict,
        requiredEmailField,
        requiredPasswordField
    ).toLens()

    val requiredContentLens = Body.webForm(
        Validator.Strict,
        requiredPostContent
    ).toLens()
    @BeforeEach
    fun setup() {
//        database.deleteAll(Users)
    }

    @Test
    fun `Login returns 400 Bad request if parameters are missing`() {
        val client = OkHttp()

        val response: Response = client(
            Request(Method.POST, "http://localhost:9999/sessions")
                .header("content-type", "application/x-www-form-urlencoded")
        )

        assertThat(response, hasStatus(Status.BAD_REQUEST))
        assertThat(response, hasBody("Invalid parameters"))
    }


    @Test
    fun `Signup returns 400 Bad request if parameters are missing`() {
        val client = OkHttp()

        val response: Response = client(
            Request(Method.POST, "http://localhost:9999/users")
                .header("content-type", "application/x-www-form-urlencoded")
        )

        assertThat(response, hasStatus(Status.BAD_REQUEST))
        assertThat(response, hasBody("Invalid parameters"))
    }
    @Test //custom test
    fun `Signup returns 200 OK response`() {
        val cookieHelper = OkHttp3CookieHelper()
        val client = OkHttp(OkHttpClient().newBuilder().cookieJar(cookieHelper.cookieJar()).build())
        val form = WebForm(
            mapOf(
                "email" to listOf("email"),
                "password" to listOf("password")
            ))

        val response: Response = client(
            Request(Method.POST, "http://localhost:9999/users").with(
                requiredSignupFormLens of form
            )
                .header("content-type", "application/x-www-form-urlencoded")
        )

        assertThat(response, hasStatus(Status.OK))
        assert(response.bodyString().contains("Login"))
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
        assert(response2.bodyString().contains("Acebook"))

        val form3 = WebForm(
            mapOf(
                "content" to listOf("content")
            )
        )

        val response3: Response = client(
            Request(Method.POST, "http://localhost:9999/posts").with(
                requiredContentLens of form3
            )
            .header("content-type", "application/x-www-form-urlencoded")
        )

        assert(response3.bodyString().contains("content"))

    }

    @Test
    fun `Get the signup page`() {
        val client = OkHttp()

        val response: Response = client(
            Request(Method.GET, "http://localhost:9999/users/new")
        )

        assertThat(response, hasStatus(Status.OK))
        assertThat(
            response,
            hasBody(Body.string(ContentType.TEXT_HTML).toLens(), containsSubstring("<h1>Sign up</h1>"))
        )
    }

    @Test
    fun `Get the login page`() {
        val client = OkHttp()

        val response: Response = client(
            Request(Method.GET, "http://localhost:9999/sessions/new")
        )

        assertThat(response, hasStatus(Status.OK))
        assertThat(
            response,
            hasBody(Body.string(ContentType.TEXT_HTML).toLens(), containsSubstring("<h1>Login</h1>"))
        )
    }

    @Test
    fun `New signed up user can sign in`() {
        val client = OkHttp()

        val signupResponse: Response = client(
            Request(Method.POST, "http://localhost:9999/users")
                .with(
                    requiredSignupFormLens of WebForm(mapOf(
                        "email" to listOf("test@acebook.com"),
                        "password" to listOf("s3cr3tp4ss")
                    ))
                )
        )

        assertThat(signupResponse, hasStatus(Status.FOUND))

        val response: Response = client(
            Request(Method.POST, "http://localhost:9999/sessions")
                .with(
                    requiredLoginCredentialsLens of WebForm(mapOf(
                        "email" to listOf("test@acebook.com"),
                        "password" to listOf("s3cr3tp4ss")
                    ))
                )
        )

        assertThat(response, hasStatus(Status.FOUND))
        assertThat(response, hasHeader("location", "/"))
        assertThat(response, hasHeader("set-cookie"))
    }
}
