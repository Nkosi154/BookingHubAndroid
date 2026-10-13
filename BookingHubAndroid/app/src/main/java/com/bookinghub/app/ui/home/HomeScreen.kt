package com.bookinghub.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.bookinghub.app.data.model.Category
import com.bookinghub.app.data.model.ProviderSummary
import com.bookinghub.app.ui.common.AvailabilityPill
import com.bookinghub.app.ui.common.RatingBadge
import com.bookinghub.app.ui.nav.BookingHubViewModelFactory

private fun iconFor(category: String): ImageVector = when (category) {
    "cleaning" -> Icons.Filled.CleaningServices
    "carwash" -> Icons.Filled.LocalCarWash
    "handyman" -> Icons.Filled.Handyman
    "salon" -> Icons.Filled.ContentCut
    else -> Icons.Filled.Category
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    factory: BookingHubViewModelFactory,
    onCategoryClick: (String, String) -> Unit,
    onProviderClick: (Int) -> Unit,
    onMyBookingsClick: () -> Unit,
    onProviderPortalClick: () -> Unit,
    onBecomeProviderClick: () -> Unit,
    onLoggedOut: () -> Unit
) {
    val viewModel: HomeViewModel = viewModel(factory = factory)
    val state = viewModel.uiState

    // Re-check provider status whenever Home comes back into view (e.g. returning
    // from "Become a provider" or the portal), not just on first load.
    LaunchedEffect(Unit) { viewModel.load() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("BookingHub", fontWeight = FontWeight.Bold)
                        state.username?.let {
                            Text("Hi, $it", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onMyBookingsClick) {
                        Icon(Icons.Filled.Receipt, contentDescription = "My bookings")
                    }
                    IconButton(onClick = { viewModel.logout(onLoggedOut) }) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Log out")
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            state.errorMessage?.let {
                item {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
            }

            item {
                if (state.isProvider) {
                    Card(
                        onClick = onProviderPortalClick,
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.Storefront, contentDescription = null, tint = Color.White)
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text("Provider Portal", color = Color.White, fontWeight = FontWeight.Bold)
                                Text(
                                    "Manage bookings, availability & earnings",
                                    color = Color.White.copy(alpha = 0.85f),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                } else {
                    Card(
                        onClick = onBecomeProviderClick,
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.Storefront, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text("Run a business?", fontWeight = FontWeight.Bold)
                                Text(
                                    "Apply to list your services on BookingHub",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            item {
                Text("Browse by category", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(12.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.height(180.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.categories) { category: Category ->
                        CategoryCard(category) { onCategoryClick(category.key, category.label) }
                    }
                }
            }

            item {
                Text("Top rated providers", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(12.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(state.featured) { provider: ProviderSummary ->
                        FeaturedProviderCard(provider) { onProviderClick(provider.id) }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryCard(category: Category, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(84.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(iconFor(category.key), contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.width(10.dp))
            Text(category.label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun FeaturedProviderCard(provider: ProviderSummary, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.width(180.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            AsyncImage(
                model = provider.photo_url,
                contentDescription = provider.name,
                modifier = Modifier.fillMaxWidth().height(100.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
            Column(Modifier.padding(10.dp)) {
                Text(provider.name, style = MaterialTheme.typography.titleMedium, maxLines = 1)
                Text(
                    provider.location,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RatingBadge(provider.rating)
                    AvailabilityPill(provider.availability_status)
                }
            }
        }
    }
}
