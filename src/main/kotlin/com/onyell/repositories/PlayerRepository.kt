package com.onyell.repositories

import com.onyell.models.PlayerModel
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface PlayerRepository : MongoRepository<PlayerModel, String> {
    fun findByUsername(username: String): PlayerModel?
}