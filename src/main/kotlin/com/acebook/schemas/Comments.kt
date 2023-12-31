package com.acebook.schemas

import com.acebook.entities.Comment
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.text

object Comments: Table<Comment>("comments") {
    val id = int("id").primaryKey().bindTo { it.id }
    val commentBody = text("comment_body").bindTo { it.commentBody }
    val userId = int("user_id").bindTo { it.userId }
    val postId = int("post_id").bindTo { it.postId }
    val dateCreated = text("date_created").bindTo { it.dateCreated }
    val authorName = text("author_name").bindTo{it.authorName}
    val authorImage = text("author_image").bindTo{it.authorImage}
    val commentsLikeCount = int("comments_like_count").bindTo {it.commentsLikeCount}


}