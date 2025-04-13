package com.onyell.config

import com.mongodb.client.*
import com.onyell.services.LoggerService
import io.github.cdimascio.dotenv.Dotenv
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MongoDB(
    private val loggerService: LoggerService,
    private val dotenv: Dotenv
) {
    @Value("\${spring.data.mongodb.uri:}")
    private var springMongoUri: String = ""
    
    private val mongoURI: String
        get() {
            val envMongoUri = dotenv["MONGODB_URI"]
            
            return when {
                !envMongoUri.isNullOrBlank() -> envMongoUri
                springMongoUri.isNotBlank() -> springMongoUri
                else -> "mongodb://localhost:27017/apiKt"
            }
        }

    @Bean
    fun mongoClient(): MongoClient {
        try {
            loggerService.info("Inicializando conexão com MongoDB...")
            val uri = mongoURI

            val safeUri = if (uri.contains("@")) {
                val atIndex = uri.indexOf("@")
                val protocolPart = uri.substringBefore("://") + "://"
                val userPart = uri.substring(protocolPart.length, uri.indexOf(":", protocolPart.length))
                val serverPart = uri.substring(atIndex)
                "$protocolPart$userPart:****$serverPart"
            } else {
                uri
            }
            
            loggerService.info("URI do MongoDB: $safeUri")
            return MongoClients.create(uri)
        } catch (e: Exception) {
            loggerService.error("Erro ao conectar com MongoDB: ${e.message}")
            throw e
        }
    }
}