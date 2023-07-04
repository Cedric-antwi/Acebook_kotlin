package com.acebook.entities

import org.ktorm.entity.Entity
import java.time.LocalDateTime

interface Like : Entity<Like> {
    companion object : Entity.Factory<Like>()
    val id: Int
    var userId: Int
    var postId: Int
}