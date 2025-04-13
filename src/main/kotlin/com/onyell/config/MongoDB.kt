package com.onyell.config

import org.springframework.context.annotation.Configuration

@Configuration
class MongoDB {
    private val mongoURI = System.getenv("MONGODB_URI") ?: "mongodb://localhost:27017"
}