package com.acebook.entities

import org.ktorm.entity.Entity

interface Friend : Entity<Friend> {
    companion object : Entity.Factory<Friend>()
    val id: Int
    val senderId: Int
    val receiverId: Int
}