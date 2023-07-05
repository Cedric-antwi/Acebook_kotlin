package com.acebook.entities

import org.ktorm.entity.Entity
import java.time.LocalDateTime

interface Comment : Entity<Comment> {
    companion object : Entity.Factory<Comment>()
    val id: Int
    var commentBody: String
    var userId: Int
    var postId: Int
    var dateCreated: LocalDateTime?
    var authorName:String

}