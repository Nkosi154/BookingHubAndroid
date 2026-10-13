package com.bookinghub.app.ui.provider

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.bookinghub.app.data.model.Review
import com.bookinghub.app.data.model.Service
import com.bookinghub.app.ui.common.AvailabilityPill
import com.bookinghub.app.ui.common.RatingBadge
import com.bookinghub.app.ui.nav.BookingHubViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderDetailScreen(
    factory: BookingHubViewModelFactory,
    providerId: Int,
    onBack: () -> Unit,
    onBookClick: (Int) -> Unit
) {
    val viewModel: ProviderDetailViewModel = viewModel(factory = factory)
    val state = viewModel.uiState

    LaunchedEffect(providerId) { viewModel.load(providerId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.provider?.name ?: "Provider") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            if (state.provider != null) {
                Surface(shadowElevation = 8.dp) {
                    Button(
                        onClick = { onBookClick(providerId) },
                        modifier = Modifier.fillMaxWidth().padding(16.dp).height(50.dp)
                    ) {
                        Text("Book this provider")
                    }
                }
            }
        }
    ) { padding ->
        when {
            state.isLoading -> Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                CircularProgressIndicator()
            }

            state.errorMessage != null -> Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                Text(state.errorMessage, color = MaterialTheme.colorScheme.error)
            }

            state.provider != null -> {
                val provider = state.provider
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        AsyncImage(
                            model = provider.photo_url,
                            contentDescription = provider.name,
                            modifier = Modifier.fillMaxWidth().height(180.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
                        )
                    }

                    item {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(provider.name, style = MaterialTheme.typography.titleLarge)
                                Text(provider.category_label, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            AvailabilityPill(provider.availability_status)
                        }
                    }

                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RatingBadge(provider.rating)
                            Spacer(Modifier.width(12.dp))
                            Icon(Icons.Filled.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text(provider.location, style = MaterialTheme.typography.bodyMedium)
                        }
                        if (provider.phone.isNotBlank()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text(provider.phone, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }

                    if (provider.bio.isNotBlank()) {
                        item {
                            Text(provider.bio, style = MaterialTheme.typography.bodyMedium)
                        }
                    }

                    item {
                        Text("Services", style = MaterialTheme.typography.titleMedium)
                    }
                    items(provider.services) { service: Service -> ServiceRow(service) }

                    item {
                        Text(
                            "${provider.timeslots.size} upcoming time slot(s) available — pick one on the booking screen.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    item {
                        Text("Reviews (${provider.reviews.size})", style = MaterialTheme.typography.titleMedium)
                    }
                    if (provider.reviews.isEmpty()) {
                        item { Text("No reviews yet.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                    }
                    items(provider.reviews) { review: Review -> ReviewRow(review) }

                    item {
                        ReviewForm(
                            isSubmitting = state.isSubmittingReview,
                            message = state.reviewMessage,
                            onSubmit = { rating, comment -> viewModel.submitReview(rating, comment) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ServiceRow(service: Service) {
    Card(shape = RoundedCornerShape(12.dp)) {
        Row(
            Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(service.name, fontWeight = FontWeight.Medium)
                if (service.description.isNotBlank()) {
                    Text(
                        service.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    "${service.duration_hours}h",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text("R${service.price}", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ReviewRow(review: Review) {
    Column(Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(review.username, fontWeight = FontWeight.Medium)
            Spacer(Modifier.width(8.dp))
            RatingBadge(review.rating.toDouble())
        }
        if (review.comment.isNotBlank()) {
            Text(review.comment, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun ReviewForm(
    isSubmitting: Boolean,
    message: String?,
    onSubmit: (Int, String) -> Unit
) {
    var rating by remember { mutableIntStateOf(5) }
    var comment by remember { mutableStateOf("") }

    Card(shape = RoundedCornerShape(12.dp)) {
        Column(Modifier.padding(12.dp)) {
            Text("Leave a review", fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(8.dp))
            Row {
                for (i in 1..5) {
                    IconButton(onClick = { rating = i }) {
                        Icon(
                            androidx.compose.material.icons.Icons.Filled.Star,
                            contentDescription = "$i stars",
                            tint = if (i <= rating) com.bookinghub.app.ui.theme.Amber
                            else MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }
            }
            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                placeholder = { Text("Share your experience...") },
                modifier = Modifier.fillMaxWidth()
            )
            message?.let {
                Spacer(Modifier.height(4.dp))
                Text(it, style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { onSubmit(rating, comment) },
                enabled = !isSubmitting,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(if (isSubmitting) "Submitting..." else "Submit review")
            }
        }
    }
}
