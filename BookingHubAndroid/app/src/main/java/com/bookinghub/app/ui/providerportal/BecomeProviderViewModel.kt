package com.bookinghub.app.ui.providerportal

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookinghub.app.data.repository.AuthRepository
import com.bookinghub.app.data.repository.ProviderPortalRepository
import kotlinx.coroutines.launch

data class BecomeProviderUiState(
    val name: String = "",
    val category: String = "cleaning",
    val bio: String = "",
    val location: String = "",
    val phone: String = "",
    val isSubmitting: Boolean = false,
    val submitted: Boolean = false,
    val errorMessage: String? = null
)

val PROVIDER_CATEGORY_OPTIONS = listOf(
    "cleaning" to "Cleaning Services",
    "carwash" to "Car Wash & Detailing",
    "handyman" to "Handyman Services",
    "salon" to "Salon & Beauty Parlor"
)

class BecomeProviderViewModel(
    private val portalRepository: ProviderPortalRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    var uiState by mutableStateOf(BecomeProviderUiState())
        private set

    fun updateName(v: String) { uiState = uiState.copy(name = v) }
    fun updateCategory(v: String) { uiState = uiState.copy(category = v) }
    fun updateBio(v: String) { uiState = uiState.copy(bio = v) }
    fun updateLocation(v: String) { uiState = uiState.copy(location = v) }
    fun updatePhone(v: String) { uiState = uiState.copy(phone = v) }

    fun submit() {
        if (uiState.name.isBlank() || uiState.location.isBlank()) {
            uiState = uiState.copy(errorMessage = "Business name and location are required.")
            return
        }
        uiState = uiState.copy(isSubmitting = true, errorMessage = null)
        viewModelScope.launch {
            portalRepository.applyToBecomeProvider(
                name = uiState.name, category = uiState.category,
                bio = uiState.bio, location = uiState.location, phone = uiState.phone
            )
                .onSuccess {
                    authRepository.refreshIsProvider() // so Home shows "Provider Portal" right away
                    uiState = uiState.copy(isSubmitting = false, submitted = true)
                }
                .onFailure { uiState = uiState.copy(isSubmitting = false, errorMessage = it.message) }
        }
    }
}
