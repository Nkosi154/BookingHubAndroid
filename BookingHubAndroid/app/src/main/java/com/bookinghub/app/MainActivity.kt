package com.bookinghub.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.bookinghub.app.ui.nav.BookingHubNavGraph
import com.bookinghub.app.ui.theme.BookingHubTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val container = (application as BookingHubApp).container

        setContent {
            BookingHubTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    BookingHubNavGraph(container)
                }
            }
        }
    }
}
