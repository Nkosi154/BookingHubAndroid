package com.bookinghub.app.data.model

data class User(
    val id: Int,
    val username: String,
    val email: String,
    val is_provider: Boolean = false,
    val provider_id: Int? = null
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class AuthResponse(
    val token: String,
    val user: User
)
