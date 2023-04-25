package com.acebook

import com.acebook.requiredLoginCredentialsLens
import com.acebook.requiredSignupFormLens
import com.acebook.schemas.Posts
import com.acebook.schemas.Users
import org.http4k.client.OkHttp
import org.http4k.core.*
import org.http4k.lens.WebForm
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.ktorm.database.Database
import org.ktorm.dsl.deleteAll

class CreatePostTest {
    @BeforeEach
    fun setup() {
        database = Database.connect("jdbc:postgresql://localhost:5432/acebook_kotlin_test")
        database.deleteAll(Posts)
        database.deleteAll(Users)
    }

    @Test
    fun `Cannot create new post if not signed in`() {
        val client = OkHttp()
        val response: Response = client(Request(Method.GET, "http://localhost:9999/posts/new"))

        assert(response.status == Status.FOUND)
        assert(response.header("Location") == "/sessions/new")
    }

    @Test
    fun `Sign up then create a new post`() {
        val client = OkHttp()
        client(
            Request(Method.POST, "http://localhost:9999/users").with(
                requiredSignupFormLens of WebForm(mapOf(
                    "email" to listOf("test@acebook.com"),
                    "password" to listOf("s3cr3tp4ss")
                ))
            )
        )

        val loginResponse = client(
            Request(Method.POST, "http://localhost:9999/sessions").with(
                requiredLoginCredentialsLens of WebForm(mapOf(
                    "email" to listOf("test@acebook.com"),
                    "password" to listOf("s3cr3tp4ss")
                ))
            )
        )

        //val response: Response = client(Request(Method.GET, "http://localhost:9999/posts/new"))

    }
}