package com.acebook.viewmodels

import com.acebook.entities.User
import org.http4k.template.ViewModel

data class ProfileSettingsViewModel(
    val currentUser: User?,

): ViewModel

