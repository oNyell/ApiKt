package com.onyell.services

import com.onyell.enums.LoggerEnum
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class LoggerService {
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")

    fun info(message: String) {
        log(LoggerEnum.INFO, message)
    }

    fun error(message: String) {
        log(LoggerEnum.ERROR, message)
    }

    fun debug(message: String) {
        log(LoggerEnum.DEBUG, message)
    }

    fun warning(message: String) {
        log(LoggerEnum.WARNING, message)
    }

    fun trace(message: String) {
        log(LoggerEnum.TRACE, message)
    }

    private fun log(level: LoggerEnum, message: String) {
        val timestamp = LocalDateTime.now().format(dateTimeFormatter)
        println("[$timestamp] [${level.getValue()}] $message")
    }
}