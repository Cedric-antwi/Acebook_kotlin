package com.acebook.entities

import org.ktorm.entity.Entity

interface Like : Entity<Like> {
    companion object : Entity.Factory<Like>()
    val id: Int
    var userId: Int
    var postId: Int
    var commentId: Int
}