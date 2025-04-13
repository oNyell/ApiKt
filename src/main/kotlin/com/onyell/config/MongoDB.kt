package com.onyell.config

import com.mongodb.client.*
import com.onyell.services.LoggerService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MongoDB(private val loggerService: LoggerService) {
    private val mongoURI = System.getenv("MONGODB_URI")

    @Bean
    fun mongoClient(): MongoClient {
        loggerService.info("Inicializando conexão com MongoDB: ${mongoURI.substring(0, mongoURI.indexOf("@"))}")
        return MongoClients.create(mongoURI)
    }
}