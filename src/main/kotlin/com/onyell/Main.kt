package com.onyell

import com.onyell.services.LoggerService
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class Main {
    
    @Bean
    fun init(loggerService: LoggerService) = CommandLineRunner {
        loggerService.info("Aplicação iniciada com sucesso!")
    }
}

fun main(args: Array<String>) {
    runApplication<Main>(*args)
}