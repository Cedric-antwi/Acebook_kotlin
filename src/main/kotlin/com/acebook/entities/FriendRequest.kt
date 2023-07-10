package com.acebook.entities

import org.ktorm.entity.Entity

interface FriendRequest : Entity<FriendRequest> {
    companion object : Entity.Factory<FriendRequest>()
    val id: Int
    var senderId: Int
    var receiverId: Int
    var requestStatus: Boolean
}