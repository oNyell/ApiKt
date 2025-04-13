package com.onyell.models

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "players")
data class PlayerModel(
    @Id val id: Int,
    var username: String,
    var password: String,
    val accountCreated: Long?,
    val firstLong: Long?,
    var lastLogin: Long?,
    var role: Int
)