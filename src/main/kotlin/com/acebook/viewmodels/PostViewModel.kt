package com.acebook.viewmodels

import com.acebook.entities.User
import org.http4k.template.ViewModel

data class PostViewModel(val content: String, val currentUser: User? = null) : ViewModel