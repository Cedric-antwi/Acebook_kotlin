package com.acebook.schemas

import com.acebook.entities.Comment
import com.acebook.entities.Like
import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.int
import org.ktorm.schema.text

object Likes: Table<Like>("likes") {
    val id = int("id").primaryKey().bindTo { it.id }
    val userId = int("user_id").bindTo { it.userId }
    val postId = int("user_id").bindTo { it.postId }
}