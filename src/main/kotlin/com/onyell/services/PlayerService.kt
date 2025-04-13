package com.onyell.services

import com.onyell.dtos.PlayerDTO
import com.onyell.models.PlayerModel
import com.onyell.repositories.PlayerRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class PlayerService(
    private val playerRepository: PlayerRepository,
    private val loggerService: LoggerService
) {
    
    fun getAllPlayers(): List<PlayerDTO> {
        loggerService.info("Serviço: Obtendo todos os jogadores")
        return playerRepository.findAll().map { PlayerDTO.fromModel(it) }
    }
    
    fun getPlayerById(id: String): PlayerDTO? {
        loggerService.info("Serviço: Obtendo jogador com ID: $id")
        val idInt = id.toIntOrNull()
        if (idInt == null) {
            loggerService.error("ID inválido fornecido: $id")
            return null
        }
        
        return playerRepository.findById(id).map { PlayerDTO.fromModel(it) }.orElse(null)
    }
    
    fun findByUsername(username: String): PlayerDTO? {
        loggerService.info("Serviço: Buscando jogador pelo username: $username")
        val player = playerRepository.findByUsername(username)
        return player?.let { PlayerDTO.fromModel(it) }
    }
    
    fun createPlayer(playerDTO: PlayerDTO): PlayerDTO {
        loggerService.info("Serviço: Criando novo jogador: ${playerDTO.username}")
        
        // Verificar se o username já existe
        playerRepository.findByUsername(playerDTO.username)?.let {
            loggerService.warning("Username já existe: ${playerDTO.username}")
            throw IllegalArgumentException("Username já existe")
        }
        
        val playerModel = playerDTO.toModel()
        val savedPlayer = playerRepository.save(playerModel)
        return PlayerDTO.fromModel(savedPlayer)
    }
    
    fun updatePlayer(id: String, playerDTO: PlayerDTO): PlayerDTO? {
        loggerService.info("Serviço: Atualizando jogador com ID: $id")
        val existingPlayerOpt = playerRepository.findById(id)
        
        if (existingPlayerOpt.isPresent) {
            val existingPlayer = existingPlayerOpt.get()
            val updatedPlayer = playerDTO.toModel(existingPlayer)
            val savedPlayer = playerRepository.save(updatedPlayer)
            return PlayerDTO.fromModel(savedPlayer)
        }
        
        loggerService.warning("Jogador com ID: $id não encontrado para atualização")
        return null
    }
    
    fun deletePlayer(id: String): Boolean {
        loggerService.info("Serviço: Excluindo jogador com ID: $id")
        val existingPlayerOpt = playerRepository.findById(id)
        
        if (existingPlayerOpt.isPresent) {
            playerRepository.deleteById(id)
            return true
        }
        
        loggerService.warning("Jogador com ID: $id não encontrado para exclusão")
        return false
    }
}