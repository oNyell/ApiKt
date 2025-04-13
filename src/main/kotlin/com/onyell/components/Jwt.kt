package com.onyell.components

import com.onyell.dtos.PlayerDTO
import org.springframework.stereotype.Component

@Component
class Jwt {

    fun genToken(playerDTO: PlayerDTO): String {
        return playerDTO.username
    }
}