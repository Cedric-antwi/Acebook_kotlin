package com.acebook.schemas

import com.acebook.entities.Friend
import org.ktorm.schema.*

object Friends: Table<Friend>("friends") {
    val id = int("id").primaryKey().bindTo { it.id }
    val senderId = int("sender_id").bindTo { it.senderId }
    val receiverId = int("receiver_id").bindTo { it.receiverId }
}