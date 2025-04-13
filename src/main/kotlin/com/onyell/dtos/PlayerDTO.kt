package com.onyell.dtos

import com.onyell.models.PlayerModel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

data class PlayerDTO(
    val id: String? = null,
    val username: String,
    val password: String? = null,
    val accountCreated: LocalDateTime? = null,
    val firstLogin: LocalDateTime? = null,
    val lastLogin: LocalDateTime? = null,
    val role: Int = 0
) {
    companion object {
        fun fromModel(model: PlayerModel): PlayerDTO {
            return PlayerDTO(
                id = model.id.toString(),
                username = model.username,
                password = null,
                accountCreated = model.accountCreated?.let { 
                    LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault()) 
                },
                firstLogin = model.firstLong?.let {
                    LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault())
                },
                lastLogin = model.lastLogin?.let { 
                    LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault()) 
                },
                role = model.role
            )
        }
    }

    fun toModel(existingModel: PlayerModel? = null): PlayerModel {
        val currentTime = System.currentTimeMillis()
        
        return existingModel?.copy(
            username = username,
            password = password ?: existingModel.password,
            lastLogin = currentTime,
            role = role
        ) ?: PlayerModel(
            id = id?.toIntOrNull() ?: 0,
            username = username,
            password = password ?: "",
            accountCreated = currentTime,
            firstLong = currentTime,
            lastLogin = currentTime,
            role = role
        )
    }
}