package com.acebook.schemas

import com.acebook.entities.Post
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.text

object Posts: Table<Post>("posts") {
    val id = int("id").primaryKey().bindTo { it.id }
    val userId = int("user_id").bindTo { it.userId }
    val content = text("content").bindTo { it.content }
}