package com.acebook

import com.acebook.entities.Post
import com.acebook.schemas.Posts
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.containsSubstring
import org.http4k.client.OkHttp
import org.http4k.core.*
import org.http4k.hamkrest.hasBody
import org.http4k.hamkrest.hasStatus
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.ktorm.database.Database
import org.ktorm.dsl.deleteAll
import org.ktorm.entity.add
import org.ktorm.entity.sequenceOf

class PostsFeedTest {

    @BeforeEach
    fun setup() {
        database = Database.connect("jdbc:postgresql://localhost:5432/acebook_kotlin_test")
        database.deleteAll(Posts)

        val newPost = Post()
        newPost.content = "Test post 1"
        val newPost2 = Post()
        newPost2.content = "Test post 2"

        database.sequenceOf(Posts).add(newPost)
        database.sequenceOf(Posts).add(newPost2)
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
}
