package com.acebook.viewmodels

import org.http4k.template.ViewModel

data class SignupViewModel(val email: String, val password: String, val errorMessage: Boolean = false) : ViewModel