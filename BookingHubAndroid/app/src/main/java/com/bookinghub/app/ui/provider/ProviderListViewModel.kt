package com.bookinghub.app.ui.provider

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookinghub.app.data.model.ProviderSummary
import com.bookinghub.app.data.repository.CatalogRepository
import kotlinx.coroutines.launch

data class ProviderListUiState(
    val isLoading: Boolean = true,
    val providers: List<ProviderSummary> = emptyList(),
    val query: String = "",
    val sort: String = "-rating",
    val errorMessage: String? = null
)

class ProviderListViewModel(private val repository: CatalogRepository) : ViewModel() {

    var uiState by mutableStateOf(ProviderListUiState())
        private set

    private var category: String = ""

    fun load(category: String) {
        this.category = category
        search(uiState.query, uiState.sort)
    }

    fun search(query: String, sort: String = uiState.sort) {
        uiState = uiState.copy(isLoading = true, query = query, sort = sort, errorMessage = null)
        viewModelScope.launch {
            repository.getProviders(category = category, query = query.ifBlank { null }, sort = sort)
                .onSuccess { uiState = uiState.copy(isLoading = false, providers = it) }
                .onFailure { uiState = uiState.copy(isLoading = false, errorMessage = it.message) }
        }
    }
}
