package com.onyell.dtos

/**
 * DTO para requisições de login
 */
data class LoginRequest(
    val username: String,
    val password: String
) 