package com.acebook.handlers

import com.acebook.*
import com.acebook.schemas.Users
import com.acebook.templateRenderer
import com.acebook.viewmodels.ListUsersViewModel
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import com.acebook.entities.User
import org.http4k.core.HttpHandler
import org.ktorm.entity.sequenceOf
import org.ktorm.entity.toList


fun listUsers(): HttpHandler = {
    val users = database.sequenceOf(Users).toList()
    val viewModel = ListUsersViewModel(users)
    Response(Status.OK).body(templateRenderer(viewModel))
}