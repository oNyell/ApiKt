package com.onyell.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Primary
import org.springframework.security.core.userdetails.UserDetailsService

/**
 * Configuração para resolver dependências circulares relacionadas ao JWT
 */
@Configuration
class JwtBeanConfig {
    
    /**
     * Cria um proxy para o UserDetailsService para quebrar a dependência circular
     */
    @Bean
    @Primary
    fun userDetailsServiceProxy(@Lazy userDetailsService: UserDetailsService): UserDetailsService {
        return userDetailsService
    }
} 