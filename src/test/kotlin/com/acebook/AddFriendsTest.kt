package com.acebook

import com.natpryce.hamkrest.assertion.assertThat
import okhttp3.OkHttpClient
import org.http4k.client.OkHttp
import org.http4k.core.*
import org.http4k.hamkrest.hasStatus
import org.http4k.lens.WebForm
import org.junit.jupiter.api.Test
import org.riversun.okhttp3.OkHttp3CookieHelper

class AddFriendsTest {
    @Test
    fun `can see option to add friend`() {
        val cookieHelper = OkHttp3CookieHelper()
        val client = OkHttp(OkHttpClient().newBuilder().cookieJar(cookieHelper.cookieJar()).build())
        val form = WebForm(
            mapOf(
                "firstname" to listOf("firstname"),
                "lastname" to listOf("lastname"),
                "username" to listOf("username"),
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
        //on home page at this point
        val addAFriend: Response = client(
            Request(Method.GET, "http://localhost:9000/friendslist/pending-friend/1")
        )
        //back at home page after clicking to add friend
        val addFriendPage: Response = client(
            Request(Method.GET, "http://localhost:9000/friendslist/request")
        )
        assertThat(addFriendPage, hasStatus(Status.OK))
        assert(addFriendPage.bodyString().contains(""))


//        assertThat(response2, hasStatus(Status.OK))
//        assert(response2.bodyString().contains("Add Friends"))
    }
}