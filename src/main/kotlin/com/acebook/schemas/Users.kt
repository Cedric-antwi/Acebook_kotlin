package com.acebook.schemas

import com.acebook.entities.User
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object Users: Table<User>("users") {
    val id = int("id").primaryKey().bindTo { it.id }
    val email = varchar("email").bindTo { it.email }
    val encryptedPassword = varchar("encrypted_password").bindTo { it.encryptedPassword }
    val firstName = varchar("first_name").bindTo { it.firstName }
    val lastName = varchar("last_name").bindTo { it.lastName }
    val username = varchar("username").bindTo { it.username }
    val image = varchar("image").bindTo { it.image }
}
