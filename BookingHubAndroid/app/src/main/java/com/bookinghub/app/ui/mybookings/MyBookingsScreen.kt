package com.bookinghub.app.ui.mybookings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.bookinghub.app.data.model.Booking
import com.bookinghub.app.ui.common.BookingStatusPill
import com.bookinghub.app.ui.nav.BookingHubViewModelFactory
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBookingsScreen(
    factory: BookingHubViewModelFactory,
    onBack: () -> Unit
) {
    val viewModel: MyBookingsViewModel = viewModel(factory = factory)
    val state = viewModel.uiState

    LaunchedEffect(Unit) { viewModel.load() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My bookings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        when {
            state.isLoading -> Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                CircularProgressIndicator()
            }

            state.errorMessage != null -> Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                Text(state.errorMessage, color = MaterialTheme.colorScheme.error)
            }

            state.bookings.isEmpty() -> Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                Text("No bookings yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.bookings) { booking: Booking -> BookingRow(booking) }
            }
        }
    }
}

@Composable
private fun BookingRow(booking: Booking) {
    Card(shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(14.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(booking.provider_name, style = MaterialTheme.typography.titleMedium)
                BookingStatusPill(booking.status_label, booking.status)
            }
            booking.service_name?.let {
                Text("$it${booking.price?.let { p -> " · R$p" } ?: ""}", fontWeight = FontWeight.Medium)
            }
            booking.start_time?.let {
                Text(formatDateTime(it), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(booking.address, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (booking.notes.isNotBlank()) {
                Text("Note: ${booking.notes}", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

private fun formatDateTime(iso: String): String = try {
    OffsetDateTime.parse(iso).format(DateTimeFormatter.ofPattern("EEE d MMM, HH:mm"))
} catch (e: DateTimeParseException) {
    iso
}
