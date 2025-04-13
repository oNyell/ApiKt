package com.onyell.config

import com.onyell.services.LoggerService
import io.github.cdimascio.dotenv.Dotenv
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import java.security.SecureRandom

@Configuration
class JwtSecurityInitializer(
    private val dotenv: Dotenv,
    private val loggerService: LoggerService
) {
    
    @Bean
    @Order(1)
    fun checkJwtSecretSecurity(): CommandLineRunner = CommandLineRunner {
        val jwtSecret = dotenv["JWT_SECRET"]
        
        if (jwtSecret.isNullOrBlank()) {
            loggerService.warning("SEGURANÇA: Nenhuma chave JWT_SECRET definida no .env, usando chave padrão. NÃO RECOMENDADO PARA PRODUÇÃO!")
        } else if (jwtSecret.length < 32) {
            loggerService.warning("SEGURANÇA: JWT_SECRET definida é muito curta (menos de 32 caracteres). Isso pode comprometer a segurança do token!")
        } else {
            loggerService.info("SEGURANÇA: JWT_SECRET configurada corretamente")
        }

        val jwtExpiration = dotenv["JWT_EXPIRATION"]?.toLongOrNull()
        if (jwtExpiration == null) {
            loggerService.warning("SEGURANÇA: Nenhum JWT_EXPIRATION definido, usando padrão de 24 horas")
        } else if (jwtExpiration > 604800000L) {
            loggerService.warning("SEGURANÇA: JWT_EXPIRATION muito longo (> 7 dias). Tokens de longa duração são menos seguros.")
        } else {
            val hours = jwtExpiration / (1000 * 60 * 60)
            loggerService.info("SEGURANÇA: JWT configurado para expirar em $hours horas")
        }
    }
    
    @Bean
    @Order(2)
    fun generateJwtSecretIfNeeded(): CommandLineRunner = CommandLineRunner {
        if (dotenv["JWT_SECRET"].isNullOrBlank()) {
            val secureRandom = SecureRandom()
            val bytes = ByteArray(64)
            secureRandom.nextBytes(bytes)
            val generatedSecret = bytes.joinToString("") { "%02x".format(it) }
            
            loggerService.info("SEGURANÇA: Chave JWT_SECRET gerada para uso: $generatedSecret")
            loggerService.info("Por favor, adicione esta chave ao seu arquivo .env:")
            loggerService.info("JWT_SECRET=$generatedSecret")
        }
    }
} 