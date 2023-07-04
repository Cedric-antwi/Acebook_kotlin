package com.acebook.entities

import org.ktorm.entity.Entity
import java.time.LocalDateTime

interface Post : Entity<Post> {
    companion object : Entity.Factory<Post>()
    val id: Int
    var content: String
    var userId: Int
    var dateCreated: LocalDateTime
}