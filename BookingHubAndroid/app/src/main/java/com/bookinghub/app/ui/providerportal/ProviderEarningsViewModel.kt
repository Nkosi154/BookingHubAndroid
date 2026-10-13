package com.bookinghub.app.ui.providerportal

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookinghub.app.data.model.ProviderEarnings
import com.bookinghub.app.data.repository.ProviderPortalRepository
import kotlinx.coroutines.launch

data class ProviderEarningsUiState(
    val isLoading: Boolean = true,
    val earnings: ProviderEarnings? = null,
    val errorMessage: String? = null
)

class ProviderEarningsViewModel(private val repository: ProviderPortalRepository) : ViewModel() {

    var uiState by mutableStateOf(ProviderEarningsUiState())
        private set

    fun load() {
        uiState = uiState.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            repository.getEarnings()
                .onSuccess { uiState = uiState.copy(isLoading = false, earnings = it) }
                .onFailure { uiState = uiState.copy(isLoading = false, errorMessage = it.message) }
        }
    }
}
