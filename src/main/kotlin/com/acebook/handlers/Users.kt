package com.acebook.handlers

import com.acebook.*
import com.acebook.entities.User
import com.acebook.schemas.Users
import com.acebook.viewmodels.SignupViewModel
import org.http4k.core.*
import org.http4k.template.HandlebarsTemplates
import org.ktorm.entity.add
import org.ktorm.entity.sequenceOf
import org.mindrot.jbcrypt.BCrypt

fun newUserHandler(): HttpHandler = {
    val renderer = HandlebarsTemplates().HotReload("src/main/resources")
    val viewModel = SignupViewModel("", "")

    Response(Status.OK).body(renderer(viewModel))
}

fun createUserHandler(): HttpHandler = { request: Request ->
    val form = requiredSignupFormLens(request)
    val inputEmail = requiredEmailField(form)
    val inputPassword = requiredPasswordField(form)
    val inputUsername = requiredUsernameField(form)
    val newUser = User {
        username = inputUsername
        email = inputEmail
        encryptedPassword = BCrypt.hashpw(inputPassword, BCrypt.gensalt())
    }

    database.sequenceOf(Users).add(newUser)

    Response(Status.FOUND).header("Location", "/sessions/new")
}