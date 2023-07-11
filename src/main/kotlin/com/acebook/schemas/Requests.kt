package com.acebook.schemas

import com.acebook.entities.Requests
import org.ktorm.schema.Table
import org.ktorm.schema.boolean
import org.ktorm.schema.int

object Requests: Table<Requests>("requests") {
    val id = int("id").primaryKey().bindTo { it.id }
    val senderId = int("sender_id").bindTo { it.senderId }
    val receiverId = int("receiver_id").bindTo { it.receiverId }
    val requestStatus = boolean("request_status").bindTo { it.requestStatus }
}