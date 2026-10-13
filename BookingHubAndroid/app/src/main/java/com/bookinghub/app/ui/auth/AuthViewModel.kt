package com.bookinghub.app.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookinghub.app.data.repository.AuthRepository
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val loggedIn: Boolean = false
)

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    var uiState by mutableStateOf(AuthUiState())
        private set

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            uiState = uiState.copy(errorMessage = "Enter your username and password.")
            return
        }
        uiState = uiState.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            repository.login(username, password)
                .onSuccess { uiState = uiState.copy(isLoading = false, loggedIn = true) }
                .onFailure { uiState = uiState.copy(isLoading = false, errorMessage = it.message) }
        }
    }

    fun register(username: String, email: String, password: String, confirmPassword: String) {
        if (username.isBlank() || email.isBlank() || password.isBlank()) {
            uiState = uiState.copy(errorMessage = "Please fill in every field.")
            return
        }
        if (password != confirmPassword) {
            uiState = uiState.copy(errorMessage = "Passwords don't match.")
            return
        }
        uiState = uiState.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            repository.register(username, email, password)
                .onSuccess { uiState = uiState.copy(isLoading = false, loggedIn = true) }
                .onFailure { uiState = uiState.copy(isLoading = false, errorMessage = it.message) }
        }
    }

    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
    }
}
