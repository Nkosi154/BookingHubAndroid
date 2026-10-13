package com.bookinghub.app.ui.providerportal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.bookinghub.app.ui.common.AvailabilityPill
import com.bookinghub.app.ui.common.RatingBadge
import com.bookinghub.app.ui.nav.BookingHubViewModelFactory
import com.bookinghub.app.ui.theme.Green

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderDashboardScreen(
    factory: BookingHubViewModelFactory,
    onBack: () -> Unit,
    onBookingsClick: () -> Unit,
    onAvailabilityClick: () -> Unit,
    onProfileClick: () -> Unit,
    onEarningsClick: () -> Unit
) {
    val viewModel: ProviderDashboardViewModel = viewModel(factory = factory)
    val state = viewModel.uiState

    LaunchedEffect(Unit) { viewModel.load() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Provider Portal") },
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

            state.dashboard != null -> {
                val dash = state.dashboard
                Column(
                    Modifier.fillMaxSize().padding(padding).padding(16.dp)
                ) {
                    if (!dash.provider.is_active) {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                        ) {
                            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.HourglassTop, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Pending review — your business isn't visible to customers yet. " +
                                        "Feel free to set up your profile and availability while you wait.",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

                    // Provider header card with status toggle
                    Card(shape = RoundedCornerShape(16.dp)) {
                        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = dash.provider.photo_url,
                                contentDescription = dash.provider.name,
                                modifier = Modifier.size(56.dp)
                                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(dash.provider.name, style = MaterialTheme.typography.titleMedium)
                                Row {
                                    RatingBadge(dash.provider.rating)
                                    Spacer(Modifier.width(8.dp))
                                    AvailabilityPill(dash.provider.availability_status)
                                }
                            }
                            Switch(
                                checked = dash.provider.availability_status == "available",
                                onCheckedChange = { viewModel.toggleStatus() },
                                enabled = !state.isTogglingStatus,
                                colors = SwitchDefaults.colors(checkedTrackColor = Green)
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Stat cards
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatCard("Total", dash.total_count.toString(), Modifier.weight(1f))
                        StatCard("Pending", dash.pending_count.toString(), Modifier.weight(1f))
                        StatCard("Confirmed", dash.confirmed_count.toString(), Modifier.weight(1f))
                    }

                    Spacer(Modifier.height(12.dp))
                    Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Total earnings", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                "R${dash.earnings}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))
                    Text("Manage", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))

                    PortalMenuRow(Icons.Filled.Receipt, "Bookings", "Confirm, cancel or complete", onBookingsClick)
                    PortalMenuRow(Icons.Filled.CalendarMonth, "Availability", "Add or remove open time slots", onAvailabilityClick)
                    PortalMenuRow(Icons.Filled.Person, "Business profile", "Name, bio, location, photo", onProfileClick)
                    PortalMenuRow(Icons.Filled.Payments, "Earnings", "This month & all-time totals", onEarningsClick)
                }
            }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(14.dp)) {
        Column(Modifier.padding(14.dp)) {
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun PortalMenuRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp)
    ) {
        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Medium)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = Color.Gray)
        }
    }
}
