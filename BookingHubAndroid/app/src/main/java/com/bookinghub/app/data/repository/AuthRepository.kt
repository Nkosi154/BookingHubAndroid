package com.bookinghub.app.data.repository

import com.bookinghub.app.data.model.AuthResponse
import com.bookinghub.app.data.model.LoginRequest
import com.bookinghub.app.data.model.RegisterRequest
import com.bookinghub.app.data.remote.ApiService
import com.bookinghub.app.data.remote.SessionManager
import com.bookinghub.app.data.remote.safeApiCall
import kotlinx.coroutines.flow.first

class AuthRepository(
    private val api: ApiService,
    private val sessionManager: SessionManager
) {
    val tokenFlow = sessionManager.tokenFlow
    val usernameFlow = sessionManager.usernameFlow
    val isProviderFlow = sessionManager.isProviderFlow

    suspend fun register(username: String, email: String, password: String): Result<AuthResponse> {
        val result = safeApiCall { api.register(RegisterRequest(username, email, password)) }
        result.onSuccess { sessionManager.saveSession(it.token, it.user.username, it.user.is_provider) }
        return result
    }

    suspend fun login(username: String, password: String): Result<AuthResponse> {
        val result = safeApiCall { api.login(LoginRequest(username, password)) }
        result.onSuccess { sessionManager.saveSession(it.token, it.user.username, it.user.is_provider) }
        return result
    }

    suspend fun logout() {
        sessionManager.clearSession()
    }

    /** Call after something changes whether the user is a provider (e.g. they just applied),
     * so Home reflects it without needing to log out and back in. */
    suspend fun refreshIsProvider(): Result<Unit> {
        val result = safeApiCall { api.me() }
        return result.map { user ->
            val token = sessionManager.tokenFlow.first()
            if (token != null) sessionManager.saveSession(token, user.username, user.is_provider)
        }
    }

    suspend fun isLoggedIn(): Boolean = sessionManager.isLoggedIn()
}
