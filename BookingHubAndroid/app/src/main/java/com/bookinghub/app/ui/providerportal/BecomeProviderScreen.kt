package com.bookinghub.app.ui.providerportal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bookinghub.app.ui.nav.BookingHubViewModelFactory
import com.bookinghub.app.ui.theme.Green

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BecomeProviderScreen(
    factory: BookingHubViewModelFactory,
    onBack: () -> Unit,
    onDone: () -> Unit
) {
    val viewModel: BecomeProviderViewModel = viewModel(factory = factory)
    val state = viewModel.uiState

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Become a provider") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (state.submitted) {
            Box(Modifier.fillMaxSize().padding(padding).padding(24.dp), Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Green, modifier = Modifier.size(72.dp))
                    Spacer(Modifier.height(16.dp))
                    Text("Application submitted!", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "We'll review it shortly. In the meantime you can already set up your " +
                            "availability and business profile from the Provider Portal.",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(24.dp))
                    Button(onClick = onDone, modifier = Modifier.fillMaxWidth().height(48.dp)) {
                        Text("Go to Provider Portal")
                    }
                }
            }
            return@Scaffold
        }

        Column(
            Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState())
        ) {
            Text(
                "Tell us about your business. Once approved, you'll appear in search " +
                    "results and customers can start booking you.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = state.name, onValueChange = viewModel::updateName,
                label = { Text("Business name") }, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            Text("Category", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(6.dp))
            Column {
                PROVIDER_CATEGORY_OPTIONS.forEach { (value, label) ->
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = state.category == value,
                            onClick = { viewModel.updateCategory(value) }
                        )
                        Text(label)
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = state.bio, onValueChange = viewModel::updateBio,
                label = { Text("About your business") }, minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = state.location, onValueChange = viewModel::updateLocation,
                label = { Text("Location") }, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = state.phone, onValueChange = viewModel::updatePhone,
                label = { Text("Phone") }, modifier = Modifier.fillMaxWidth()
            )

            state.errorMessage?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(20.dp))
            Button(
                onClick = { viewModel.submit() },
                enabled = !state.isSubmitting,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text(if (state.isSubmitting) "Submitting..." else "Submit application")
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
