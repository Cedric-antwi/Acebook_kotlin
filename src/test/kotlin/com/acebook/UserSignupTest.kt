package com.acebook

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.containsSubstring
import okhttp3.OkHttpClient
import org.http4k.client.OkHttp
import org.http4k.core.*
import org.http4k.hamkrest.*
import org.http4k.lens.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.riversun.okhttp3.OkHttp3CookieHelper

class UserSignupTest {
    val requiredEmailField = FormField.nonEmptyString().required("email")
    val requiredPasswordField = FormField.nonEmptyString().required("password")
    val requiredFirstName = FormField.nonEmptyString().required("firstname")
    val requiredLastName = FormField.nonEmptyString().required("lastname")
    val requiredUsername = FormField.nonEmptyString().required("username")
    val requiredSignupFormLens = Body.webForm(
        Validator.Strict,
        requiredEmailField,
        requiredPasswordField,
        requiredFirstName,
        requiredLastName,
        requiredUsername
    ).toLens()

    val requiredLoginCredentialsLens = Body.webForm(
        Validator.Strict,
        requiredEmailField,
        requiredPasswordField
    ).toLens()

    @BeforeEach
    fun setup() {
    }

    @Test
    fun `Login returns 400 Bad request if parameters are missing`() {
        val client = OkHttp()
        val response: Response = client(
            Request(Method.POST, "http://localhost:9999/sessions")
                .header("content-type", "application/x-www-form-urlencoded")
        )
        assertThat(response, hasStatus(Status.BAD_REQUEST))
        assertThat(response, hasBody("Invalid parameters 1"))
    }

    @Test
    fun `Signup returns 400 Bad request if parameters are missing`() {
        val client = OkHttp()
        val response: Response = client(
            Request(Method.POST, "http://localhost:9999/users")
                .header("content-type", "application/x-www-form-urlencoded")
        )
        assertThat(response, hasStatus(Status.BAD_REQUEST))
        assertThat(response, hasBody("Invalid parameters 1"))
    }

    @Test
    fun `Testing the signup and login functionality`() {
        val cookieHelper = OkHttp3CookieHelper()
        val client = OkHttp(OkHttpClient().newBuilder().cookieJar(cookieHelper.cookieJar()).build())
        val form = WebForm(
            mapOf(
                "email" to listOf("test@acebook.com"),
                "password" to listOf("s3cr3tp4ss"),
                "firstname" to listOf("Tester"),
                "lastname" to listOf("User"),
                "username" to listOf("Tester_User"))
            )

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
                com.acebook.requiredLoginCredentialsLens of form2
            )
                .header("content-type", "application/x-www-form-urlencoded")
        )

        assertThat(response2, hasStatus(Status.OK))
        assert(response2.bodyString().contains("Acebook"))
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
            hasBody(Body.string(ContentType.TEXT_HTML).toLens(), containsSubstring("<h1 class=\"title\">Sign up to AceBook</h1><br><br>"))
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
            hasBody(Body.string(ContentType.TEXT_HTML).toLens(), containsSubstring("<h1 class=\"title\">Login</h1>"))
        )
    }

    @Test
    fun `New signed up user can sign in`() {
        val cookieHelper = OkHttp3CookieHelper()
        val client = OkHttp(OkHttpClient().newBuilder().cookieJar(cookieHelper.cookieJar()).build())
        val response: Response = client(
            Request(Method.POST, "http://localhost:9999/sessions")
                .with(
                    requiredLoginCredentialsLens of WebForm(mapOf(
                        "email" to listOf("test@acebook.com"),
                        "password" to listOf("s3cr3tp4ss")
                    ))
                )
        )
          assertThat(response, hasStatus(Status.OK))
          assert(response.bodyString().contains("Welcome"))
    }
}
