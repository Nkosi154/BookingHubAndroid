package com.bookinghub.app.ui.booking

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.bookinghub.app.BookingHubApp
import com.bookinghub.app.data.repository.BookingRepository
import kotlinx.coroutines.launch

/** Must match the callback_url the Django API sets in InitiatePaymentView (api/views.py). */
private const val CALLBACK_HOST = "bookinghub.local"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentWebViewScreen(
    bookingId: Int,
    checkoutUrl: String,
    onVerified: (success: Boolean) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isVerifying by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val bookingRepository: BookingRepository = remember {
        (context.applicationContext as BookingHubApp).container.bookingRepository
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Secure payment") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        webViewClient = object : WebViewClient() {
                            @SuppressLint("WebViewClientOnReceivedSslError")
                            override fun shouldOverrideUrlLoading(
                                view: WebView,
                                request: android.webkit.WebResourceRequest
                            ): Boolean {
                                val url = request.url
                                if (url.host == CALLBACK_HOST) {
                                    val reference = url.getQueryParameter("reference")
                                        ?: url.getQueryParameter("trxref")
                                    isVerifying = true
                                    scope.launch {
                                        val result = if (reference != null) {
                                            bookingRepository.verifyPayment(bookingId, reference)
                                        } else {
                                            Result.failure(Exception("No payment reference returned."))
                                        }
                                        isVerifying = false
                                        result
                                            .onSuccess { onVerified(it.payment_verified) }
                                            .onFailure {
                                                errorMessage = it.message
                                                onVerified(false)
                                            }
                                    }
                                    return true // never actually resolve bookinghub.local
                                }
                                return false
                            }
                        }
                        loadUrl(checkoutUrl)
                    }
                }
            )

            if (isVerifying) {
                Box(
                    Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.35f)),
                    contentAlignment = Alignment.Center
                ) {
                    Card {
                        Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(Modifier.height(12.dp))
                            Text("Confirming your payment...")
                        }
                    }
                }
            }

            errorMessage?.let {
                Box(Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.BottomCenter) {
                    Card {
                        Text(it, modifier = Modifier.padding(12.dp), color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}
