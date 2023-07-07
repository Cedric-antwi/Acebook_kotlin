package com.acebook.entities

import org.ktorm.entity.Entity

interface Requests : Entity<Requests> {
    companion object : Entity.Factory<Requests>()
    val id: Int
    val senderId: Int
    val receiverId: Int
    val requestStatus: Boolean
}