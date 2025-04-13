package com.onyell.controllers

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Controlador para testar os diferentes níveis de acesso JWT
 * Fornece endpoints para acessos públicos, autenticados, e administrativos
 */
@RestController
@RequestMapping
class TestController {

    /**
     * Endpoint público que pode ser acessado sem autenticação
     * @return Mensagem de teste
     */
    @GetMapping("/api/public/test")
    fun publicTest(): Map<String, String> {
        return mapOf(
            "message" to "Este é um endpoint público que pode ser acessado sem autenticação!"
        )
    }

    /**
     * Endpoint que requer autenticação (usuário ou admin)
     * @param userDetails Detalhes do usuário autenticado
     * @return Informações do usuário autenticado
     */
    @GetMapping("/api/players/test")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    fun playerTest(@AuthenticationPrincipal userDetails: UserDetails): Map<String, Any> {
        return mapOf(
            "message" to "Este é um endpoint protegido que só pode ser acessado por usuários autenticados!",
            "user" to mapOf(
                "username" to userDetails.username,
                "roles" to userDetails.authorities.map { it.authority }
            )
        )
    }

    /**
     * Endpoint que requer role de ADMIN
     * @param userDetails Detalhes do usuário autenticado
     * @return Informações do usuário admin
     */
    @GetMapping("/api/admin/test")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    fun adminTest(@AuthenticationPrincipal userDetails: UserDetails): Map<String, Any> {
        return mapOf(
            "message" to "Este é um endpoint administrativo protegido que só pode ser acessado por administradores!",
            "user" to mapOf(
                "username" to userDetails.username,
                "roles" to userDetails.authorities.map { it.authority }
            )
        )
    }
} 