package com.bookinghub.app.ui.providerportal

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookinghub.app.data.model.ProviderDashboard
import com.bookinghub.app.data.repository.ProviderPortalRepository
import kotlinx.coroutines.launch

data class ProviderDashboardUiState(
    val isLoading: Boolean = true,
    val dashboard: ProviderDashboard? = null,
    val isTogglingStatus: Boolean = false,
    val errorMessage: String? = null
)

class ProviderDashboardViewModel(private val repository: ProviderPortalRepository) : ViewModel() {

    var uiState by mutableStateOf(ProviderDashboardUiState())
        private set

    fun load() {
        uiState = uiState.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            repository.getDashboard()
                .onSuccess { uiState = uiState.copy(isLoading = false, dashboard = it) }
                .onFailure { uiState = uiState.copy(isLoading = false, errorMessage = it.message) }
        }
    }

    fun toggleStatus() {
        uiState = uiState.copy(isTogglingStatus = true)
        viewModelScope.launch {
            repository.toggleStatus()
                .onSuccess { load() } // refresh whole dashboard so counts/earnings stay in sync
                .onFailure { uiState = uiState.copy(isTogglingStatus = false, errorMessage = it.message) }
        }
    }
}
