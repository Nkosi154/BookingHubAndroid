package com.bookinghub.app.ui.provider

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookinghub.app.data.model.ProviderDetail
import com.bookinghub.app.data.repository.CatalogRepository
import kotlinx.coroutines.launch

data class ProviderDetailUiState(
    val isLoading: Boolean = true,
    val provider: ProviderDetail? = null,
    val errorMessage: String? = null,
    val isSubmittingReview: Boolean = false,
    val reviewMessage: String? = null
)

class ProviderDetailViewModel(private val repository: CatalogRepository) : ViewModel() {

    var uiState by mutableStateOf(ProviderDetailUiState())
        private set

    private var providerId: Int = -1

    fun load(providerId: Int) {
        this.providerId = providerId
        uiState = uiState.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            repository.getProviderDetail(providerId)
                .onSuccess { uiState = uiState.copy(isLoading = false, provider = it) }
                .onFailure { uiState = uiState.copy(isLoading = false, errorMessage = it.message) }
        }
    }

    fun submitReview(rating: Int, comment: String) {
        uiState = uiState.copy(isSubmittingReview = true, reviewMessage = null)
        viewModelScope.launch {
            repository.postReview(providerId, rating, comment)
                .onSuccess {
                    uiState = uiState.copy(isSubmittingReview = false, reviewMessage = "Thanks for your review!")
                    load(providerId)
                }
                .onFailure {
                    uiState = uiState.copy(isSubmittingReview = false, reviewMessage = it.message)
                }
        }
    }
}
