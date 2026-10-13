package com.bookinghub.app.ui.providerportal

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookinghub.app.data.model.TimeSlot
import com.bookinghub.app.data.repository.ProviderPortalRepository
import kotlinx.coroutines.launch

data class ProviderAvailabilityUiState(
    val isLoading: Boolean = true,
    val slots: List<TimeSlot> = emptyList(),
    val selectedDate: String = "", // "YYYY-MM-DD"
    val selectedHours: Set<Int> = emptySet(),
    val isSaving: Boolean = false,
    val message: String? = null,
    val errorMessage: String? = null
)

class ProviderAvailabilityViewModel(private val repository: ProviderPortalRepository) : ViewModel() {

    var uiState by mutableStateOf(ProviderAvailabilityUiState())
        private set

    fun load() {
        uiState = uiState.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            repository.getAvailability()
                .onSuccess { uiState = uiState.copy(isLoading = false, slots = it) }
                .onFailure { uiState = uiState.copy(isLoading = false, errorMessage = it.message) }
        }
    }

    fun setDate(date: String) {
        uiState = uiState.copy(selectedDate = date)
    }

    fun toggleHour(hour: Int) {
        val current = uiState.selectedHours
        uiState = uiState.copy(selectedHours = if (hour in current) current - hour else current + hour)
    }

    fun addSlots() {
        if (uiState.selectedDate.isBlank() || uiState.selectedHours.isEmpty()) {
            uiState = uiState.copy(errorMessage = "Pick a date and at least one hour.")
            return
        }
        uiState = uiState.copy(isSaving = true, errorMessage = null, message = null)
        viewModelScope.launch {
            repository.addTimeSlots(uiState.selectedDate, uiState.selectedHours.sorted())
                .onSuccess {
                    uiState = uiState.copy(
                        isSaving = false, selectedHours = emptySet(),
                        message = "${it.created_count} slot(s) added.", slots = it.slots
                    )
                }
                .onFailure { uiState = uiState.copy(isSaving = false, errorMessage = it.message) }
        }
    }

    fun deleteSlot(slotId: Int) {
        viewModelScope.launch {
            repository.deleteTimeSlot(slotId)
                .onSuccess { load() }
                .onFailure { uiState = uiState.copy(errorMessage = it.message) }
        }
    }
}
