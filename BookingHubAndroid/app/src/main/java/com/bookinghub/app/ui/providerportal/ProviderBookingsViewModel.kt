package com.bookinghub.app.ui.providerportal

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookinghub.app.data.model.CustomerBooking
import com.bookinghub.app.data.repository.ProviderPortalRepository
import kotlinx.coroutines.launch

data class ProviderBookingsUiState(
    val isLoading: Boolean = true,
    val bookings: List<CustomerBooking> = emptyList(),
    val filterStatus: String = "",
    val actioningBookingId: Int? = null,
    val errorMessage: String? = null
)

class ProviderBookingsViewModel(private val repository: ProviderPortalRepository) : ViewModel() {

    var uiState by mutableStateOf(ProviderBookingsUiState())
        private set

    fun load(filterStatus: String = uiState.filterStatus) {
        uiState = uiState.copy(isLoading = true, filterStatus = filterStatus, errorMessage = null)
        viewModelScope.launch {
            repository.getBookings(filterStatus.ifBlank { null })
                .onSuccess { uiState = uiState.copy(isLoading = false, bookings = it) }
                .onFailure { uiState = uiState.copy(isLoading = false, errorMessage = it.message) }
        }
    }

    /** action is "confirm" | "cancel" | "complete" — mirrors the website's booking action buttons. */
    fun act(bookingId: Int, action: String) {
        uiState = uiState.copy(actioningBookingId = bookingId, errorMessage = null)
        viewModelScope.launch {
            repository.actOnBooking(bookingId, action)
                .onSuccess {
                    uiState = uiState.copy(actioningBookingId = null)
                    load()
                }
                .onFailure { uiState = uiState.copy(actioningBookingId = null, errorMessage = it.message) }
        }
    }
}
