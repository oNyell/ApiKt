package com.onyell.components

import com.onyell.dtos.PlayerDTO
import com.onyell.models.PlayerModel
import com.onyell.services.LoggerService
import io.github.cdimascio.dotenv.Dotenv
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class Jwt(
    private val dotenv: Dotenv,
    private val loggerService: LoggerService
) {
    private val jwtSecret: String
        get() = dotenv["JWT_SECRET"] ?: "chave_secreta_padrao_para_desenvolvimento_local_apenas"

    private val jwtExpirationMs: Long
        get() = dotenv["JWT_EXPIRATION"]?.toLongOrNull() ?: 86400000L

    private fun getSigningKey(): SecretKey {
        val keyBytes = jwtSecret.toByteArray()
        return Keys.hmacShaKeyFor(keyBytes)
    }

    fun getExpirationTimeInMillis(): Long {
        return jwtExpirationMs
    }

    /**
     * Gera um token JWT para o usuário - contém username e role
     */
    fun generateToken(playerDTO: PlayerDTO): String {
        loggerService.info("Gerando token JWT para usuário: ${playerDTO.username}")
        
        val now = Date()
        val expiryDate = Date(now.time + jwtExpirationMs)

        val role = playerDTO.role

        val token = Jwts.builder()
            .setSubject(playerDTO.username)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .claim("role", "ROLE_$role")
            .signWith(getSigningKey())
            .compact()
        
        loggerService.info("Token JWT gerado com sucesso, expira em: $expiryDate")
        return token
    }

    fun generateToken(player: PlayerModel): String {
        return generateToken(PlayerDTO.fromModel(player))
    }

    fun validateTokenAndGetClaims(token: String): Jws<Claims>? {
        if (token.isBlank()) {
            loggerService.error("Token JWT vazio")
            return null
        }
        
        loggerService.info("Validando token JWT: ${token.take(20)}...")
        
        return try {
            val claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
            
            val expiration = claims.body.expiration
            val now = Date()
            
            if (expiration.before(now)) {
                loggerService.error("Token JWT expirado. Expirou em: $expiration, agora: $now")
                return null
            }
            
            loggerService.info("Token JWT válido. Username: ${claims.body.subject}, expira em: ${claims.body.expiration}")
            claims
        } catch (ex: JwtException) {
            loggerService.error("Token JWT inválido: ${ex.message}")
            null
        } catch (ex: IllegalArgumentException) {
            loggerService.error("Token JWT inválido (argumento inválido): ${ex.message}")
            null
        } catch (ex: Exception) {
            loggerService.error("Erro desconhecido ao validar token JWT: ${ex.message}")
            ex.printStackTrace()
            null
        }
    }

    fun getUsernameFromToken(token: String): String? {
        val claims = validateTokenAndGetClaims(token)
        val username = claims?.body?.subject
        loggerService.info("Username extraído do token: $username")
        return username
    }

    fun getRoleFromToken(token: String): String? {
        val claims = validateTokenAndGetClaims(token)
        val role = claims?.body?.get("role", String::class.java)
        loggerService.info("Role extraída do token: $role")
        return role
    }

    fun isTokenValid(token: String): Boolean {
        val isValid = validateTokenAndGetClaims(token) != null
        loggerService.info("Token válido: $isValid")
        return isValid
    }
}