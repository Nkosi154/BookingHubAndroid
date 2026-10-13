package com.bookinghub.app.ui.booking

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookinghub.app.data.model.ProviderDetail
import com.bookinghub.app.data.model.Service
import com.bookinghub.app.data.model.TimeSlot
import com.bookinghub.app.data.repository.BookingRepository
import com.bookinghub.app.data.repository.CatalogRepository
import kotlinx.coroutines.launch

data class BookingUiState(
    val isLoading: Boolean = true,
    val provider: ProviderDetail? = null,
    val selectedService: Service? = null,
    val selectedSlot: TimeSlot? = null,
    val address: String = "",
    val notes: String = "",
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val checkoutUrl: String? = null,
    val createdBookingId: Int? = null
)

class BookingViewModel(
    private val catalogRepository: CatalogRepository,
    private val bookingRepository: BookingRepository
) : ViewModel() {

    var uiState by mutableStateOf(BookingUiState())
        private set

    fun load(providerId: Int) {
        uiState = uiState.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            catalogRepository.getProviderDetail(providerId)
                .onSuccess { uiState = uiState.copy(isLoading = false, provider = it) }
                .onFailure { uiState = uiState.copy(isLoading = false, errorMessage = it.message) }
        }
    }

    fun selectService(service: Service) {
        uiState = uiState.copy(selectedService = service)
    }

    fun selectSlot(slot: TimeSlot) {
        uiState = uiState.copy(selectedSlot = slot)
    }

    fun updateAddress(value: String) {
        uiState = uiState.copy(address = value)
    }

    fun updateNotes(value: String) {
        uiState = uiState.copy(notes = value)
    }

    /** Creates the booking, then immediately initialises the Paystack transaction. */
    fun submitAndPay() {
        val provider = uiState.provider ?: return
        if (uiState.address.isBlank()) {
            uiState = uiState.copy(errorMessage = "Please add the address for this booking.")
            return
        }
        if (uiState.selectedService == null) {
            uiState = uiState.copy(errorMessage = "Please choose a service.")
            return
        }

        uiState = uiState.copy(isSubmitting = true, errorMessage = null)
        viewModelScope.launch {
            val bookingResult = bookingRepository.createBooking(
                providerId = provider.id,
                serviceId = uiState.selectedService?.id,
                timeslotId = uiState.selectedSlot?.id,
                address = uiState.address,
                notes = uiState.notes
            )

            bookingResult.onFailure {
                uiState = uiState.copy(isSubmitting = false, errorMessage = it.message)
                return@launch
            }

            val booking = bookingResult.getOrNull()!!
            val paymentResult = bookingRepository.initiatePayment(booking.id)

            paymentResult
                .onSuccess {
                    uiState = uiState.copy(
                        isSubmitting = false,
                        createdBookingId = booking.id,
                        checkoutUrl = it.authorization_url
                    )
                }
                .onFailure {
                    // Booking exists but payment couldn't start — the person can retry
                    // from "My Bookings", which shows it as Awaiting Payment.
                    uiState = uiState.copy(
                        isSubmitting = false,
                        createdBookingId = booking.id,
                        errorMessage = "Booking saved, but payment couldn't start: ${it.message}"
                    )
                }
        }
    }
}
