package com.onyell.config

import com.onyell.services.LoggerService
import io.github.cdimascio.dotenv.Dotenv
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.server.ConfigurableWebServerFactory
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Component
class ServerConfig(
    private val dotenv: Dotenv,
    private val loggerService: LoggerService,
    private val environment: Environment
) : WebServerFactoryCustomizer<ConfigurableWebServerFactory> {

    @Value("\${server.port:8080}")
    private var springServerPort: Int = 8080

    override fun customize(factory: ConfigurableWebServerFactory) {
        // Verifica qual perfil está ativo
        val activeProfiles = environment.activeProfiles
        val isProd = activeProfiles.contains("prod")
        val isDev = activeProfiles.contains("dev")
        
        // Obtém a porta baseada no perfil
        val serverPort = when {
            isProd -> dotenv["SERVER_PORT_PROD"]?.toIntOrNull()
            isDev -> dotenv["SERVER_PORT_DEV"]?.toIntOrNull()
            else -> dotenv["SERVER_PORT"]?.toIntOrNull()
        } ?: springServerPort
        
        loggerService.info("Configurando servidor na porta: $serverPort (perfil: ${if (activeProfiles.isEmpty()) "default" else activeProfiles.joinToString()})")
        factory.setPort(serverPort)
    }
} 