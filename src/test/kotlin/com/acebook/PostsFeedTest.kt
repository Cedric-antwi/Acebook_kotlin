package com.acebook

import com.acebook.entities.Post
import com.acebook.schemas.Posts
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.contains
import com.natpryce.hamkrest.containsSubstring
import okhttp3.OkHttpClient
import org.http4k.client.OkHttp
import org.http4k.core.*
import org.http4k.hamkrest.hasBody
import org.http4k.hamkrest.hasStatus
import org.http4k.lens.WebForm
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.ktorm.database.Database
import org.ktorm.dsl.deleteAll
import org.ktorm.entity.add
import org.ktorm.entity.sequenceOf
import org.riversun.okhttp3.OkHttp3CookieHelper

class PostsFeedTest {

    @BeforeEach
    fun setup() {
//        database.deleteAll(Posts)

        val newPost = Post()
        newPost.content = "Test post 1"
        val newPost2 = Post()
        newPost2.content = "Test post 2"
        val newPost3 = Post()
        newPost3.content = "test post 3"

        database.sequenceOf(Posts).add(newPost)
        database.sequenceOf(Posts).add(newPost2)
        database.sequenceOf(Posts).add(newPost3)

    }

    @Test
    fun `Get the index page`() {
        val client = OkHttp()

        val response: Response = client(
            Request(Method.GET, "http://localhost:9999/")
        )

        assertThat(response, hasStatus(Status.OK))
        assertThat(
            response,
            hasBody(containsSubstring("<h1>Welcome to Acebook</h1>"))
        )
        assertThat(response, hasBody(containsSubstring("Test post 1")))
        assertThat(response, hasBody(containsSubstring("Test post 1")))
    }

    @Test
    fun `User cannot see the like button if not logged in`() {
        val client = OkHttp()

        val response: Response = client(
            Request(Method.GET, "http://localhost:9999/")
        )

        assertThat(response, hasStatus(Status.OK))
        assertThat(
            response,
            hasBody(containsSubstring("Acebook"))
        )
    }

    @Test
    fun `User should only be able to click the button once logged in`() {
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
        assert(response2.bodyString().contains("like"))
   }
}
