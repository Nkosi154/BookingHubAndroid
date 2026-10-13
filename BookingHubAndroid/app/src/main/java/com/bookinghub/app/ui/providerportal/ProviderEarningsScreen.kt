package com.bookinghub.app.ui.providerportal

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
import com.bookinghub.app.data.model.CustomerBooking
import com.bookinghub.app.ui.nav.BookingHubViewModelFactory
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderEarningsScreen(
    factory: BookingHubViewModelFactory,
    onBack: () -> Unit
) {
    val viewModel: ProviderEarningsViewModel = viewModel(factory = factory)
    val state = viewModel.uiState

    LaunchedEffect(Unit) { viewModel.load() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Earnings") },
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
            state.earnings != null -> {
                val e = state.earnings
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            EarningsCard("All-time", "R${e.total}", "${e.total_count} paid booking(s)", Modifier.weight(1f))
                            EarningsCard("This month", "R${e.month_total}", "${e.month_count} paid booking(s)", Modifier.weight(1f))
                        }
                    }
                    item {
                        Spacer(Modifier.height(4.dp))
                        Text("Paid bookings", style = MaterialTheme.typography.titleMedium)
                    }
                    if (e.paid_bookings.isEmpty()) {
                        item { Text("No paid bookings yet.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                    }
                    items(e.paid_bookings) { booking: CustomerBooking -> EarningsRow(booking) }
                }
            }
        }
    }
}

@Composable
private fun EarningsCard(label: String, amount: String, sub: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(16.dp)) {
            Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(amount, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(sub, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun EarningsRow(booking: CustomerBooking) {
    Card(shape = RoundedCornerShape(12.dp)) {
        Row(
            Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(booking.customer_username, fontWeight = FontWeight.Medium)
                booking.service_name?.let {
                    Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(
                    formatDate(booking.created_at),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text("R${booking.amount_paid ?: "0.00"}", fontWeight = FontWeight.Bold)
        }
    }
}

private fun formatDate(iso: String): String = try {
    OffsetDateTime.parse(iso).format(DateTimeFormatter.ofPattern("d MMM yyyy"))
} catch (e: DateTimeParseException) {
    iso
}
