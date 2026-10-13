package com.bookinghub.app.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookinghub.app.data.model.Category
import com.bookinghub.app.data.model.ProviderSummary
import com.bookinghub.app.data.repository.AuthRepository
import com.bookinghub.app.data.repository.CatalogRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = true,
    val categories: List<Category> = emptyList(),
    val featured: List<ProviderSummary> = emptyList(),
    val username: String? = null,
    val isProvider: Boolean = false,
    val errorMessage: String? = null
)

class HomeViewModel(
    private val catalogRepository: CatalogRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    var uiState by mutableStateOf(HomeUiState())
        private set

    init {
        load()
    }

    fun load() {
        uiState = uiState.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            val username = authRepository.usernameFlow.first()
            val isProvider = authRepository.isProviderFlow.first()
            val categoriesResult = catalogRepository.getCategories()
            val featuredResult = catalogRepository.getProviders(sort = "-rating")

            uiState = uiState.copy(
                isLoading = false,
                username = username,
                isProvider = isProvider,
                categories = categoriesResult.getOrDefault(emptyList()),
                featured = featuredResult.getOrDefault(emptyList()).take(6),
                errorMessage = categoriesResult.exceptionOrNull()?.message
                    ?: featuredResult.exceptionOrNull()?.message
            )
        }
    }

    fun logout(onDone: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onDone()
        }
    }
}
