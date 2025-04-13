package com.onyell.controllers

import com.onyell.annotations.Tag
import com.onyell.components.Jwt
import com.onyell.dtos.LoginRequest
import com.onyell.dtos.PlayerDTO
import com.onyell.models.PlayerModel
import com.onyell.repositories.PlayerRepository
import com.onyell.services.LoggerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.GetMapping

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação", description = "Endpoints para autenticação e registro de usuários")
class AuthController {
    
    @Autowired
    @Lazy
    private lateinit var authenticationManager: AuthenticationManager
    
    @Autowired
    private lateinit var playerRepository: PlayerRepository
    
    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder
    
    @Autowired
    private lateinit var jwt: Jwt
    
    @Autowired
    private lateinit var loggerService: LoggerService

    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<Map<String, Any>> {
        loggerService.info("Tentativa de login para o usuário: ${loginRequest.username}")
        
        try {
            val authentication: Authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(loginRequest.username, loginRequest.password)
            )

            SecurityContextHolder.getContext().authentication = authentication

            val player = playerRepository.findByUsername(loginRequest.username)
                ?: throw UsernameNotFoundException("Usuário não encontrado")

            player.lastLogin = System.currentTimeMillis()
            playerRepository.save(player)

            val token = jwt.generateToken(player)
            
            loggerService.info("Login bem-sucedido para o usuário: ${loginRequest.username}")

            return ResponseEntity.ok(mapOf(
                "token" to token,
                "type" to "Bearer",
                "username" to player.username,
                "role" to (player.role),
                "expiresIn" to jwt.getExpirationTimeInMillis()
            ))
        } catch (e: Exception) {
            loggerService.error("Falha no login para o usuário ${loginRequest.username}: ${e.message}")
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(mapOf("error" to "Credenciais inválidas"))
        }
    }
    
    @PostMapping("/register")
    fun register(@RequestBody playerDTO: PlayerDTO): ResponseEntity<Any> {
        loggerService.info("Tentativa de registro para o usuário: ${playerDTO.username}")

        if (playerRepository.findByUsername(playerDTO.username) != null) {
            loggerService.warning("Tentativa de registro com nome de usuário já existente: ${playerDTO.username}")
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(mapOf("error" to "Usuário já existe"))
        }
        
        try {

            val player = PlayerModel(
                id = playerDTO.id?.toIntOrNull() ?: 0,
                username = playerDTO.username,
                password = passwordEncoder.encode(playerDTO.password ?: ""),
                accountCreated = System.currentTimeMillis(),
                firstLong = System.currentTimeMillis(),
                lastLogin = System.currentTimeMillis(),
                role = playerDTO.role
            )

            val savedPlayer = playerRepository.save(player)

            val token = jwt.generateToken(savedPlayer)
            
            loggerService.info("Registro bem-sucedido para o usuário: ${playerDTO.username}")

            return ResponseEntity.status(HttpStatus.CREATED).body(mapOf(
                "token" to token,
                "type" to "Bearer",
                "username" to savedPlayer.username,
                "role" to (savedPlayer.role),
                "expiresIn" to jwt.getExpirationTimeInMillis()
            ))
        } catch (e: Exception) {
            loggerService.error("Erro ao registrar o usuário ${playerDTO.username}: ${e.message}")
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Falha ao registrar o usuário"))
        }
    }

    @GetMapping("/me")
    fun getCurrentUser(): ResponseEntity<Any> {
        val auth = SecurityContextHolder.getContext().authentication
        
        if (auth == null || auth.name == "anonymousUser" || !auth.isAuthenticated) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(mapOf(
                    "error" to "Usuário não autenticado",
                    "authDetails" to mapOf(
                        "exists" to (auth != null),
                        "name" to (auth?.name ?: "null"),
                        "isAuthenticated" to (auth?.isAuthenticated ?: false)
                    )
                ))
        }

        val userDetails = auth.principal
        
        return ResponseEntity.ok(mapOf(
            "authenticationClass" to auth.javaClass.name,
            "name" to auth.name,
            "authorities" to auth.authorities.map { it.authority },
            "isAuthenticated" to auth.isAuthenticated,
            "principalClass" to (userDetails?.javaClass?.name ?: "null"),
            "principalDetails" to (if (userDetails is UserDetails) {
                mapOf(
                    "username" to userDetails.username,
                    "authorities" to userDetails.authorities.map { it.authority },
                    "enabled" to userDetails.isEnabled,
                    "accountNonExpired" to userDetails.isAccountNonExpired,
                    "accountNonLocked" to userDetails.isAccountNonLocked,
                    "credentialsNonExpired" to userDetails.isCredentialsNonExpired
                )
            } else null),
            "credentials" to auth.credentials,
            "details" to auth.details
        ))
    }
} 