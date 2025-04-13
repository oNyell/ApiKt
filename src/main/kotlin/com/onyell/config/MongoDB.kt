package com.onyell.config

import com.mongodb.client.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MongoDB {
    private val mongoURI = System.getenv("MONGODB_URI")

    @Bean
    fun mongoClient(): MongoClient {
        return MongoClients.create(mongoURI)
    }
}