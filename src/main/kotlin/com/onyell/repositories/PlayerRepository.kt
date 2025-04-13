package com.onyell.repositories

import com.onyell.models.PlayerModel
import org.springframework.data.mongodb.repository.MongoRepository

interface PlayerRepository : MongoRepository<PlayerModel, String>