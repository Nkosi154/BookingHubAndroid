package com.bookinghub.app.ui.provider

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.bookinghub.app.data.model.ProviderSummary
import com.bookinghub.app.ui.common.AvailabilityPill
import com.bookinghub.app.ui.common.RatingBadge
import com.bookinghub.app.ui.nav.BookingHubViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderListScreen(
    factory: BookingHubViewModelFactory,
    category: String,
    categoryLabel: String,
    onBack: () -> Unit,
    onProviderClick: (Int) -> Unit
) {
    val viewModel: ProviderListViewModel = viewModel(factory = factory)
    val state = viewModel.uiState

    LaunchedEffect(category) { viewModel.load(category) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(categoryLabel) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = state.query,
                onValueChange = { viewModel.search(it) },
                placeholder = { Text("Search by name or area") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            when {
                state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }

                state.errorMessage != null -> Text(state.errorMessage, color = MaterialTheme.colorScheme.error)

                state.providers.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No providers found.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(state.providers) { provider: ProviderSummary ->
                        ProviderRow(provider) { onProviderClick(provider.id) }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProviderRow(provider: ProviderSummary, onClick: () -> Unit) {
    Card(onClick = onClick, shape = RoundedCornerShape(16.dp)) {
        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = provider.photo_url,
                contentDescription = provider.name,
                modifier = Modifier.size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(provider.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    provider.location,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    RatingBadge(provider.rating)
                    AvailabilityPill(provider.availability_status)
                }
            }
        }
    }
}
