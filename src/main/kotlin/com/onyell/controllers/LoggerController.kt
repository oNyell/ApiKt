package com.onyell.controllers

import com.onyell.annotations.Tag
import com.onyell.services.LoggerService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/logger")
@Tag(name = "Controlador de Logs", description = "Controller para testar o serviço de logging")
class LoggerController(private val loggerService: LoggerService) {

    @GetMapping("/info")
    fun logInfo(@RequestParam message: String): String {
        loggerService.info(message)
        return "Mensagem INFO registrada: $message"
    }

    @GetMapping("/error")
    fun logError(@RequestParam message: String): String {
        loggerService.error(message)
        return "Mensagem ERROR registrada: $message"
    }

    @GetMapping("/debug")
    fun logDebug(@RequestParam message: String): String {
        loggerService.debug(message)
        return "Mensagem DEBUG registrada: $message"
    }

    @GetMapping("/warning")
    fun logWarning(@RequestParam message: String): String {
        loggerService.warning(message)
        return "Mensagem WARNING registrada: $message"
    }

    @GetMapping("/trace")
    fun logTrace(@RequestParam message: String): String {
        loggerService.trace(message)
        return "Mensagem TRACE registrada: $message"
    }
}