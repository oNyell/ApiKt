package com.onyell.config

import com.onyell.models.PlayerModel
import com.onyell.repositories.PlayerRepository
import com.onyell.services.LoggerService
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order

@Configuration
class DataInitializerConfig(
    private val playerRepository: PlayerRepository,
    private val loggerService: LoggerService
) {
    
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    fun initDefaultUsers() = CommandLineRunner {
        val adminUsername = "admin"
        
        // Verificar se o admin já existe
        if (playerRepository.findByUsername(adminUsername) == null) {
            loggerService.info("Criando usuário administrador padrão")
            
            val adminUser = PlayerModel(
                id = 1,
                username = adminUsername,
                password = "admin123",  // Em produção, usar senha criptografada
                accountCreated = System.currentTimeMillis(),
                firstLong = System.currentTimeMillis(),
                lastLogin = System.currentTimeMillis(),
                role = 1
            )
            
            playerRepository.save(adminUser)
            loggerService.info("Usuário administrador criado com sucesso: $adminUsername")
        } else {
            loggerService.info("Usuário administrador já existe: $adminUsername")
        }
        
        // Criar um usuário regular (opcional)
        val regularUsername = "user"
        if (playerRepository.findByUsername(regularUsername) == null) {
            loggerService.info("Criando usuário regular padrão")
            
            val regularUser = PlayerModel(
                id = 2,
                username = regularUsername,
                password = "user123",  // Em produção, usar senha criptografada
                accountCreated = System.currentTimeMillis(),
                firstLong = System.currentTimeMillis(),
                lastLogin = System.currentTimeMillis(),
                role = 0
            )
            
            playerRepository.save(regularUser)
            loggerService.info("Usuário regular criado com sucesso: $regularUsername")
        } else {
            loggerService.info("Usuário regular já existe: $regularUsername")
        }
    }
} 