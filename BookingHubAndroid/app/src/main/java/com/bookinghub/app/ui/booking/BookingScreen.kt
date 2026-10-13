package com.bookinghub.app.ui.booking

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bookinghub.app.data.model.Service
import com.bookinghub.app.data.model.TimeSlot
import com.bookinghub.app.ui.nav.BookingHubViewModelFactory
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    factory: BookingHubViewModelFactory,
    providerId: Int,
    onBack: () -> Unit,
    onPaymentReady: (bookingId: Int, checkoutUrl: String) -> Unit
) {
    val viewModel: BookingViewModel = viewModel(factory = factory)
    val state = viewModel.uiState

    LaunchedEffect(providerId) { viewModel.load(providerId) }

    LaunchedEffect(state.checkoutUrl, state.createdBookingId) {
        val url = state.checkoutUrl
        val id = state.createdBookingId
        if (url != null && id != null) onPaymentReady(id, url)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Book · ${state.provider?.name ?: ""}") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading || state.provider == null) {
            Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) { CircularProgressIndicator() }
            return@Scaffold
        }

        val provider = state.provider

        Column(
            Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState())
        ) {
            Text("1. Choose a service", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            provider.services.forEach { service: Service ->
                SelectableRow(
                    label = "${service.name} — R${service.price}",
                    selected = state.selectedService?.id == service.id,
                    onClick = { viewModel.selectService(service) }
                )
            }

            Spacer(Modifier.height(20.dp))
            Text("2. Choose a time slot (optional)", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            if (provider.timeslots.isEmpty()) {
                Text(
                    "No open slots right now — the provider will confirm a time after booking.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                provider.timeslots.forEach { slot: TimeSlot ->
                    SelectableRow(
                        label = formatSlot(slot.start_time),
                        selected = state.selectedSlot?.id == slot.id,
                        onClick = { viewModel.selectSlot(slot) }
                    )
                }
            }

            Spacer(Modifier.height(20.dp))
            Text("3. Your details", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = state.address,
                onValueChange = viewModel::updateAddress,
                label = { Text("Address") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = state.notes,
                onValueChange = viewModel::updateNotes,
                label = { Text("Notes for the provider (optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            state.errorMessage?.let {
                Spacer(Modifier.height(12.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(20.dp))
            Button(
                onClick = { viewModel.submitAndPay() },
                enabled = !state.isSubmitting,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                if (state.isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("Continue to payment")
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SelectableRow(label: String, selected: Boolean, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            else MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .selectable(selected = selected, onClick = onClick)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(selected = selected, onClick = onClick)
            Spacer(Modifier.width(8.dp))
            Text(label, fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal)
        }
    }
}

private fun formatSlot(isoStart: String): String {
    return try {
        val dt = OffsetDateTime.parse(isoStart)
        dt.format(DateTimeFormatter.ofPattern("EEE d MMM, HH:mm"))
    } catch (e: DateTimeParseException) {
        isoStart
    }
}
