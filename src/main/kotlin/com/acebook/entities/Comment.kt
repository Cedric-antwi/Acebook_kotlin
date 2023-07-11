package com.acebook.entities

import org.ktorm.entity.Entity

interface Comment : Entity<Comment> {
    companion object : Entity.Factory<Comment>()
    val id: Int
    var commentBody: String
    var userId: Int
    var postId: Int
    var dateCreated: String
    var authorName:String
    var authorImage: String
    var commentsLikeCount: Int

}