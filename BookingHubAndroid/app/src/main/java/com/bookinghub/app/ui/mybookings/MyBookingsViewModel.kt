package com.bookinghub.app.ui.mybookings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookinghub.app.data.model.Booking
import com.bookinghub.app.data.repository.BookingRepository
import kotlinx.coroutines.launch

data class MyBookingsUiState(
    val isLoading: Boolean = true,
    val bookings: List<Booking> = emptyList(),
    val errorMessage: String? = null
)

class MyBookingsViewModel(private val repository: BookingRepository) : ViewModel() {

    var uiState by mutableStateOf(MyBookingsUiState())
        private set

    fun load() {
        uiState = uiState.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            repository.getMyBookings()
                .onSuccess { uiState = uiState.copy(isLoading = false, bookings = it) }
                .onFailure { uiState = uiState.copy(isLoading = false, errorMessage = it.message) }
        }
    }
}
