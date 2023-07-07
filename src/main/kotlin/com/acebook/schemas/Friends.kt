package com.acebook.schemas

import com.acebook.entities.Friends
import com.acebook.schemas.Posts.bindTo
import com.acebook.schemas.Posts.primaryKey
import org.ktorm.schema.*

object Friends: Table<Friends>("friends") {
    val id = int("id").primaryKey().bindTo { it.id }
    val senderId = int("sender_id").bindTo { it.senderId }
    val receiverId = int("receiver_id").bindTo { it.receiverId }
    val friendStatus = boolean("friend_status").bindTo { it.friendStatus }
}