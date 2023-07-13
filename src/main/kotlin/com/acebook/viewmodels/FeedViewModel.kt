package com.acebook.viewmodels

import com.acebook.entities.Post
import com.acebook.entities.User
import org.http4k.template.ViewModel

data class FeedViewModel(val posts: List<Post>, val currentUser: User?, val myFriends: MutableList<FriendRequestViewModel>) : ViewModel