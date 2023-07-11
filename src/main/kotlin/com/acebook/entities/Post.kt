package com.acebook.entities

import org.ktorm.entity.Entity

interface Post : Entity<Post> {
    companion object : Entity.Factory<Post>()
    val id: Int
    var content: String
    var userId: Int
    var dateCreated: String
    var authorName:String
    var authorImage: String?
    var likesCount: Int
    var postImage: String?

}