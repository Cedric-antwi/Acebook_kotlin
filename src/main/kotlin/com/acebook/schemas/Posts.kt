package com.acebook.schemas

import com.acebook.entities.Post
import com.acebook.schemas.Posts.bindTo
import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.int
import org.ktorm.schema.text

object Posts: Table<Post>("posts") {
    val id = int("id").primaryKey().bindTo { it.id }
    val userId = int("user_id").bindTo { it.userId }
    val content = text("content").bindTo { it.content }
    val dateCreated = datetime("date_created").bindTo { it.dateCreated }
    val authorName = text("author_name").bindTo{it.authorName}
    val authorImage = text("author_image").bindTo { it.authorImage }
    val likesCount = int("likes_count").bindTo {it.likesCount}
    val postImage = text("post_image").bindTo{it.postImage}

}