package com.bookinghub.app.ui.providerportal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bookinghub.app.data.model.AVAILABLE_HOUR_CHOICES
import com.bookinghub.app.data.model.TimeSlot
import com.bookinghub.app.ui.nav.BookingHubViewModelFactory
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderAvailabilityScreen(
    factory: BookingHubViewModelFactory,
    onBack: () -> Unit
) {
    val viewModel: ProviderAvailabilityViewModel = viewModel(factory = factory)
    val state = viewModel.uiState
    var dateText by remember { mutableStateOf(LocalDate.now().plusDays(1).toString()) }

    LaunchedEffect(Unit) {
        viewModel.load()
        viewModel.setDate(dateText)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Availability") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState())
        ) {
            Text("Add new slots", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = dateText,
                onValueChange = { dateText = it; viewModel.setDate(it) },
                label = { Text("Date (YYYY-MM-DD)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            Text("Hours", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(6.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.height(180.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(AVAILABLE_HOUR_CHOICES) { (hour, label) ->
                    FilterChip(
                        selected = hour in state.selectedHours,
                        onClick = { viewModel.toggleHour(hour) },
                        label = { Text(label) }
                    )
                }
            }

            state.errorMessage?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
            state.message?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.primary)
            }

            Spacer(Modifier.height(12.dp))
            Button(
                onClick = { viewModel.addSlots() },
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text(if (state.isSaving) "Saving..." else "Add slots")
            }

            Spacer(Modifier.height(24.dp))
            Text("Upcoming slots", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            if (state.isLoading) {
                CircularProgressIndicator()
            } else if (state.slots.isEmpty()) {
                Text("No upcoming slots yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                state.slots.forEach { slot: TimeSlot ->
                    SlotRow(slot, onDelete = { viewModel.deleteSlot(slot.id) })
                }
            }
        }
    }
}

@Composable
private fun SlotRow(slot: TimeSlot, onDelete: () -> Unit) {
    Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(formatSlot(slot.start_time), fontWeight = FontWeight.Medium)
                if (slot.is_booked) {
                    Text("Booked", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                }
            }
            if (!slot.is_booked) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Close, contentDescription = "Remove slot")
                }
            }
        }
    }
}

private fun formatSlot(iso: String): String = try {
    OffsetDateTime.parse(iso).format(DateTimeFormatter.ofPattern("EEE d MMM, HH:mm"))
} catch (e: DateTimeParseException) {
    iso
}
