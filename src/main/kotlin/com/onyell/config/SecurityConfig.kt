package com.onyell.config

import com.onyell.services.LoggerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.core.authority.SimpleGrantedAuthority

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
class SecurityConfig @Autowired constructor(
    private val loggerService: LoggerService
) {
    
    @Bean
    fun securityFilterChain(http: HttpSecurity, jwtAuthenticationFilter: com.onyell.config.JwtAuthenticationFilter): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests { authorize ->
                authorize
                    .requestMatchers("/", "/index.html", "/static/**", "/*.js", "/*.css", "/*.ico", "/favicon.ico").permitAll()
                    .requestMatchers("/api/auth/**").permitAll()
                    .requestMatchers("/api/public/**").permitAll()
                    .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")
                    .requestMatchers("/api/players/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                    .anyRequest().authenticated()
            }
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
        
        loggerService.info("Configuração de segurança: JWT configurado como método primário de autenticação")
        
        return http.build()
    }
    
    @Bean
    fun userDetailsService(): UserDetailsService {
        val userDetailsManager = InMemoryUserDetailsManager()
        
        val admin = User.builder()
            .username("admin")
            .password(passwordEncoder().encode("admin123"))
            .authorities(listOf(SimpleGrantedAuthority("ROLE_ADMIN")))
            .build()
        
        val user = User.builder()
            .username("user")
            .password(passwordEncoder().encode("user123"))
            .authorities(listOf(SimpleGrantedAuthority("ROLE_USER")))
            .build()
        
        userDetailsManager.createUser(admin)
        userDetailsManager.createUser(user)
        
        loggerService.info("Usuário admin criado com authorities: ROLE_ADMIN")
        loggerService.info("Usuário user criado com authorities: ROLE_USER")
        
        return userDetailsManager
    }
    
    @Bean
    fun authenticationManager(userDetailsService: UserDetailsService): AuthenticationManager {
        val authProvider = DaoAuthenticationProvider()
        authProvider.setUserDetailsService(userDetailsService)
        authProvider.setPasswordEncoder(passwordEncoder())
        return ProviderManager(authProvider)
    }
    
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
} 