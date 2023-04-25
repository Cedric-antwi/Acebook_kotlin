package com.acebook.entities

import org.ktorm.entity.Entity

interface User : Entity<User> {
    companion object : Entity.Factory<User>()
    val id: Int
    var email: String
    var encryptedPassword: String
}