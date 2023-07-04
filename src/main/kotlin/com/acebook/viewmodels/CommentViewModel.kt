package com.acebook.viewmodels

import com.acebook.entities.Comment
import com.acebook.entities.Post
import com.acebook.entities.User
import org.http4k.template.ViewModel

data class CommentViewModel(val comment: List<Comment>, val currentPost: Post, val currentUser: User? = null) : ViewModel