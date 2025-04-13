package com.onyell.controllers

import com.onyell.annotations.Tag
import com.onyell.dtos.PlayerDTO
import com.onyell.services.LoggerService
import com.onyell.services.PlayerService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/players")
@Tag(name = "Controlador de Usuários", description = "Aqui será o controller dos usuários, liberando ENDPOINTS para o client-side")
class PlayerController(
    private val playerService: PlayerService,
    private val loggerService: LoggerService
) {

    @GetMapping
    fun getAllPlayers(): ResponseEntity<List<PlayerDTO>> {
        loggerService.info("Endpoint: Buscando todos os jogadores")
        return ResponseEntity.ok(playerService.getAllPlayers())
    }
    
    @GetMapping("/{id}")
    fun getPlayerById(@PathVariable id: String): ResponseEntity<PlayerDTO> {
        loggerService.info("Endpoint: Buscando jogador com ID: $id")
        val player = playerService.getPlayerById(id)
        return if (player != null) {
            ResponseEntity.ok(player)
        } else {
            ResponseEntity.notFound().build()
        }
    }
    
    @GetMapping("/username/{username}")
    fun getPlayerByUsername(@PathVariable username: String): ResponseEntity<PlayerDTO> {
        loggerService.info("Endpoint: Buscando jogador com username: $username")
        val player = playerService.findByUsername(username)
        return if (player != null) {
            ResponseEntity.ok(player)
        } else {
            ResponseEntity.notFound().build()
        }
    }
    
    @PostMapping
    fun createPlayer(@RequestBody playerDTO: PlayerDTO): ResponseEntity<Any> {
        loggerService.info("Endpoint: Criando novo jogador")
        return try {
            val createdPlayer = playerService.createPlayer(playerDTO)
            ResponseEntity.status(HttpStatus.CREATED).body(createdPlayer)
        } catch (e: IllegalArgumentException) {
            loggerService.error("Erro ao criar jogador: ${e.message}")
            ResponseEntity.status(HttpStatus.CONFLICT)
                .body(mapOf("error" to (e.message ?: "Username já existe")))
        } catch (e: Exception) {
            loggerService.error("Erro inesperado ao criar jogador: ${e.message}")
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Erro interno do servidor"))
        }
    }
    
    @PutMapping("/{id}")
    fun updatePlayer(
        @PathVariable id: String,
        @RequestBody playerDTO: PlayerDTO
    ): ResponseEntity<Any> {
        loggerService.info("Endpoint: Atualizando jogador com ID: $id")
        return try {
            val updatedPlayer = playerService.updatePlayer(id, playerDTO)
            if (updatedPlayer != null) {
                ResponseEntity.ok(updatedPlayer)
            } else {
                ResponseEntity.notFound().build()
            }
        } catch (e: Exception) {
            loggerService.error("Erro ao atualizar jogador: ${e.message}")
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Erro ao atualizar jogador"))
        }
    }
    
    @DeleteMapping("/{id}")
    fun deletePlayer(@PathVariable id: String): ResponseEntity<Any> {
        loggerService.info("Endpoint: Excluindo jogador com ID: $id")
        return try {
            val deleted = playerService.deletePlayer(id)
            if (deleted) {
                ResponseEntity.noContent().build()
            } else {
                ResponseEntity.notFound().build()
            }
        } catch (e: Exception) {
            loggerService.error("Erro ao excluir jogador: ${e.message}")
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Erro ao excluir jogador"))
        }
    }
}