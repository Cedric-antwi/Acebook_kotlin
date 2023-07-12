package com.acebook.entities

import org.ktorm.entity.Entity

interface Friends : Entity<Friends> {
    companion object : Entity.Factory<Friends>()
    val id: Int
    val senderId: Int
    val receiverId: Int
}