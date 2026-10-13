package com.bookinghub.app.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bookinghub.app.ui.theme.Amber
import com.bookinghub.app.ui.theme.Green
import com.bookinghub.app.ui.theme.Red

@Composable
fun RatingBadge(rating: Double) {
    androidx.compose.foundation.layout.Row(
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.Star, contentDescription = null, tint = Amber, modifier = Modifier.size(16.dp))
        androidx.compose.foundation.layout.Spacer(Modifier.width(2.dp))
        Text(String.format("%.1f", rating), style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun AvailabilityPill(status: String) {
    val (label, color) = when (status) {
        "available" -> "Available" to Green
        "busy" -> "Busy" to Red
        else -> status to Color.Gray
    }
    Text(
        text = label,
        color = Color.White,
        fontSize = 11.sp,
        modifier = Modifier
            .background(color, RoundedCornerShape(50))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    )
}

@Composable
fun BookingStatusPill(statusLabel: String, status: String) {
    val color = when (status) {
        "confirmed", "completed" -> Green
        "pending" -> Amber
        "cancelled" -> Red
        else -> Color.Gray // awaiting_payment
    }
    Text(
        text = statusLabel,
        color = Color.White,
        fontSize = 11.sp,
        modifier = Modifier
            .background(color, RoundedCornerShape(50))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    )
}
