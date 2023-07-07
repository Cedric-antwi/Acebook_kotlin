package com.acebook.viewmodels

import com.acebook.entities.User
import com.acebook.schemas.Users
import org.http4k.template.ViewModel

data class ListUsersViewModel(
    val users: List<User>
): ViewModel