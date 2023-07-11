package com.acebook.schemas

import com.acebook.entities.Like
import org.ktorm.schema.Table
import org.ktorm.schema.int


object Likes: Table<Like>("likes") {
    val id = int("id").primaryKey().bindTo { it.id }
    val userId = int("user_id").bindTo { it.userId }
    val postId = int("post_id").bindTo { it.postId }
    val commentId = int("comment_id").bindTo { it.commentId }
}