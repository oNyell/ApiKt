package com.onyell.config

import com.onyell.components.Jwt
import com.onyell.repositories.PlayerRepository
import com.onyell.services.LoggerService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter : OncePerRequestFilter() {
    
    @Autowired
    private lateinit var jwt: Jwt
    
    @Autowired
    @Lazy
    private lateinit var userDetailsService: UserDetailsService
    
    @Autowired
    private lateinit var playerRepository: PlayerRepository
    
    @Autowired
    private lateinit var loggerService: LoggerService

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val token = getTokenFromRequest(request)
            
            loggerService.info("Processando requisição para: ${request.method} ${request.requestURI}")
            
            if (token != null) {
                loggerService.info("Token JWT encontrado na requisição")
                
                if (jwt.isTokenValid(token)) {
                    val username = jwt.getUsernameFromToken(token)
                    
                    if (username != null) {
                        loggerService.info("Username extraído do token: $username")
                        
                        try {
                            val userDetails = userDetailsService.loadUserByUsername(username)
                            loggerService.info("UserDetails carregado: $username, authorities: ${userDetails.authorities}")

                            val tokenRole = jwt.getRoleFromToken(token)
                            loggerService.info("Role extraída do token: $tokenRole")

                            val authorities = if (tokenRole != null) {
                                val formattedRole = if (tokenRole.startsWith("ROLE_")) tokenRole else "ROLE_$tokenRole"
                                loggerService.info("Role extraída do token e formatada: $formattedRole")
                                setOf(SimpleGrantedAuthority(formattedRole))
                            } else {
                                userDetails.authorities
                            }
                            
                            loggerService.info("Authorities finais: $authorities")

                            val authentication = UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                authorities
                            )

                            SecurityContextHolder.getContext().authentication = authentication
                            
                            loggerService.info("Usuário autenticado via JWT: $username com roles: $authorities")

                            val auth = SecurityContextHolder.getContext().authentication
                            if (auth != null) {
                                loggerService.info("Autenticação confirmada no contexto: ${auth.name}, authorities: ${auth.authorities}")
                            } else {
                                loggerService.error("ERRO: Autenticação não foi registrada no contexto de segurança!")
                            }
                            
                        } catch (ex: Exception) {
                            loggerService.error("Erro ao carregar detalhes do usuário: ${ex.message}")
                            ex.printStackTrace()
                        }
                    } else {
                        loggerService.error("Não foi possível extrair o username do token")
                    }
                } else {
                    loggerService.error("Token JWT inválido")
                }
            } else {
                loggerService.info("Nenhum token JWT encontrado na requisição")
            }
        } catch (e: Exception) {
            loggerService.error("Erro geral na autenticação: ${e.message}")
            e.printStackTrace()
        }
        
        filterChain.doFilter(request, response)
    }
    
    private fun getTokenFromRequest(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        loggerService.info("Bearer token: $bearerToken")
        
        return if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7)
        } else {
            null
        }
    }
} 