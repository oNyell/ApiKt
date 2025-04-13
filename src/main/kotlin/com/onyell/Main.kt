package com.onyell

import com.onyell.services.LoggerService
import io.github.cdimascio.dotenv.Dotenv
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.core.env.Environment

@SpringBootApplication
class Main {
    
    @Value("\${spring.data.mongodb.uri}")
    private lateinit var mongoUri: String
    
    @Bean
    fun init(loggerService: LoggerService, env: Environment, dotenv: Dotenv) = CommandLineRunner {
        loggerService.info("Aplicação iniciada com sucesso!")
        
        val activeProfiles = if (env.activeProfiles.isEmpty()) "default" else env.activeProfiles.joinToString()
        loggerService.info("Perfis ativos: $activeProfiles")

        val dotenvVars = dotenv.entries()
            .filter { it.key != "MONGODB_URI" }
            .map { it.key }
        
        if (dotenvVars.isNotEmpty()) {
            loggerService.info("Variáveis carregadas do arquivo .env: ${dotenvVars.joinToString(", ")}")
        }

        if (dotenv["MONGODB_URI"] != null) {
            loggerService.info("MONGODB_URI definida no arquivo .env")
        } else {
            loggerService.info("MONGODB_URI não definida no arquivo .env, usando configuração do Spring")
        }

        val safeMongoUri = if (mongoUri.contains("@")) {
            val atIndex = mongoUri.indexOf("@")
            val protocolPart = mongoUri.substringBefore("://") + "://"
            val authPart = mongoUri.substring(protocolPart.length, mongoUri.indexOf(":", protocolPart.length)) + ":****"
            val serverPart = mongoUri.substring(atIndex)
            protocolPart + authPart + serverPart
        } else {
            mongoUri
        }
        
        loggerService.info("Conectado ao MongoDB: $safeMongoUri")
    }
}

fun main(args: Array<String>) {
    runApplication<Main>(*args)
}