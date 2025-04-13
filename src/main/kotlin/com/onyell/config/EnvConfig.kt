package com.onyell.config

import com.onyell.services.LoggerService
import io.github.cdimascio.dotenv.Dotenv
import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.MapPropertySource

@Configuration
class EnvConfig(
    private val environment: ConfigurableEnvironment,
    private val loggerService: LoggerService
) {
    
    @PostConstruct
    fun init() {
        try {
            val dotenv = Dotenv.configure()
                .directory("./")
                .ignoreIfMissing()
                .load()
            
            val envMap = dotenv.entries().associate { it.key to it.value }
            
            if (envMap.isNotEmpty()) {
                environment.propertySources.addFirst(
                    MapPropertySource("dotenvProperties", envMap)
                )
                loggerService.info("Arquivo .env carregado com sucesso com ${envMap.size} propriedades")
            } else {
                loggerService.warning("Arquivo .env está vazio ou não foi encontrado")
            }
        } catch (e: Exception) {
            loggerService.error("Erro ao carregar arquivo .env: ${e.message}")
        }
    }
    
    @Bean
    fun dotenv(): Dotenv {
        return Dotenv.configure()
            .directory("./")
            .ignoreIfMissing()
            .load()
    }
} 