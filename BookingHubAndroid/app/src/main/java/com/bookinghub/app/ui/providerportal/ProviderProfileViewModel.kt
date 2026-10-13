package com.bookinghub.app.ui.providerportal

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookinghub.app.data.model.ProviderProfile
import com.bookinghub.app.data.repository.ProviderPortalRepository
import kotlinx.coroutines.launch

data class ProviderProfileUiState(
    val isLoading: Boolean = true,
    val profile: ProviderProfile? = null,
    val name: String = "",
    val bio: String = "",
    val location: String = "",
    val phone: String = "",
    val isSaving: Boolean = false,
    val message: String? = null,
    val errorMessage: String? = null
)

class ProviderProfileViewModel(private val repository: ProviderPortalRepository) : ViewModel() {

    var uiState by mutableStateOf(ProviderProfileUiState())
        private set

    fun load() {
        uiState = uiState.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            repository.getProfile()
                .onSuccess {
                    uiState = uiState.copy(
                        isLoading = false, profile = it,
                        name = it.name, bio = it.bio, location = it.location, phone = it.phone
                    )
                }
                .onFailure { uiState = uiState.copy(isLoading = false, errorMessage = it.message) }
        }
    }

    fun updateName(v: String) { uiState = uiState.copy(name = v) }
    fun updateBio(v: String) { uiState = uiState.copy(bio = v) }
    fun updateLocation(v: String) { uiState = uiState.copy(location = v) }
    fun updatePhone(v: String) { uiState = uiState.copy(phone = v) }

    fun save() {
        uiState = uiState.copy(isSaving = true, message = null, errorMessage = null)
        viewModelScope.launch {
            repository.updateProfile(
                name = uiState.name, bio = uiState.bio,
                location = uiState.location, phone = uiState.phone
            )
                .onSuccess { uiState = uiState.copy(isSaving = false, profile = it, message = "Profile updated!") }
                .onFailure { uiState = uiState.copy(isSaving = false, errorMessage = it.message) }
        }
    }
}
