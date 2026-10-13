package com.bookinghub.app.ui.providerportal

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bookinghub.app.data.model.CustomerBooking
import com.bookinghub.app.ui.common.BookingStatusPill
import com.bookinghub.app.ui.nav.BookingHubViewModelFactory
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

private val STATUS_TABS = listOf(
    "" to "All", "pending" to "Pending", "confirmed" to "Confirmed",
    "completed" to "Completed", "cancelled" to "Cancelled"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderBookingsScreen(
    factory: BookingHubViewModelFactory,
    onBack: () -> Unit
) {
    val viewModel: ProviderBookingsViewModel = viewModel(factory = factory)
    val state = viewModel.uiState

    LaunchedEffect(Unit) { viewModel.load() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bookings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            Row(
                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                STATUS_TABS.forEach { (value, label) ->
                    FilterChip(
                        selected = state.filterStatus == value,
                        onClick = { viewModel.load(value) },
                        label = { Text(label) }
                    )
                }
            }

            state.errorMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(horizontal = 16.dp))
            }

            when {
                state.isLoading -> Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }

                state.bookings.isEmpty() -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text("No bookings here.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.bookings) { booking: CustomerBooking ->
                        ProviderBookingCard(
                            booking = booking,
                            isBusy = state.actioningBookingId == booking.id,
                            onConfirm = { viewModel.act(booking.id, "confirm") },
                            onCancel = { viewModel.act(booking.id, "cancel") },
                            onComplete = { viewModel.act(booking.id, "complete") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProviderBookingCard(
    booking: CustomerBooking,
    isBusy: Boolean,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onComplete: () -> Unit
) {
    Card(shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(14.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(booking.customer_username, style = MaterialTheme.typography.titleMedium)
                BookingStatusPill(booking.status_label, booking.status)
            }
            booking.service_name?.let {
                Text("$it${booking.amount_paid?.let { p -> " · R$p" } ?: ""}", fontWeight = FontWeight.Medium)
            }
            booking.start_time?.let { Text(formatDateTime(it), color = MaterialTheme.colorScheme.onSurfaceVariant) }
            Text(booking.address, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (booking.notes.isNotBlank()) {
                Text("Note: ${booking.notes}", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                when (booking.status) {
                    "pending" -> {
                        Button(onClick = onConfirm, enabled = !isBusy, modifier = Modifier.weight(1f)) {
                            Text("Confirm")
                        }
                        OutlinedButton(onClick = onCancel, enabled = !isBusy, modifier = Modifier.weight(1f)) {
                            Text("Reject")
                        }
                    }
                    "confirmed" -> {
                        Button(onClick = onComplete, enabled = !isBusy, modifier = Modifier.weight(1f)) {
                            Text("Mark completed")
                        }
                        OutlinedButton(onClick = onCancel, enabled = !isBusy, modifier = Modifier.weight(1f)) {
                            Text("Cancel")
                        }
                    }
                }
            }
        }
    }
}

private fun formatDateTime(iso: String): String = try {
    OffsetDateTime.parse(iso).format(DateTimeFormatter.ofPattern("EEE d MMM, HH:mm"))
} catch (e: DateTimeParseException) {
    iso
}
