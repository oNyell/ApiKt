package com.onyell.controllers

import com.onyell.annotations.Tag
import com.onyell.services.LoggerService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/players")
@Tag(name = "Controlador de Usuários", description = "Aqui será o controller dos usuários, liberando ENDPOINTS para o client-side")
class PlayerController(private val loggerService: LoggerService) {

    @GetMapping
    fun getPlayers(): String {
        loggerService.info("Buscando todos os jogadores")
        return "Lista de jogadores"
    }
}