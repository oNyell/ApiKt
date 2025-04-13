package com.onyell.services

import org.springframework.stereotype.Service

@Service
class PlayerService(private val loggerService: LoggerService) {
    
    fun getAllPlayers(): List<String> {
        loggerService.info("Serviço: Obtendo todos os jogadores")
        return listOf("Jogador 1", "Jogador 2", "Jogador 3")
    }
    
    fun getPlayerById(id: String): String {
        loggerService.info("Serviço: Obtendo jogador com ID: $id")
        return "Jogador $id"
    }
}