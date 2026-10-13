package com.bookinghub.app.data.model

/** A booking as seen by the PROVIDER managing it — shows the customer, not themselves. */
data class CustomerBooking(
    val id: Int,
    val customer_username: String,
    val service_name: String?,
    val start_time: String?,
    val address: String,
    val notes: String,
    val status: String,
    val status_label: String,
    val amount_paid: String?,
    val payment_verified: Boolean,
    val created_at: String
)

data class BookingActionRequest(
    val action: String // "confirm" | "cancel" | "complete"
)

data class ProviderProfile(
    val id: Int,
    val name: String,
    val category: String,
    val category_label: String,
    val bio: String,
    val location: String,
    val phone: String,
    val rating: Double,
    val availability_status: String,
    val photo_url: String?,
    val is_active: Boolean = true
)

data class ProviderDashboard(
    val provider: ProviderProfile,
    val total_count: Int,
    val pending_count: Int,
    val confirmed_count: Int,
    val earnings: String
)

data class AddTimeSlotsRequest(
    val date: String, // "YYYY-MM-DD"
    val hours: List<Int>
)

data class AddTimeSlotsResponse(
    val created_count: Int,
    val slots: List<TimeSlot>
)

data class ProviderEarnings(
    val total: String,
    val month_total: String,
    val month_count: Int,
    val total_count: Int,
    val paid_bookings: List<CustomerBooking>
)

/** The hours a provider can toggle when adding availability — mirrors bookings/forms.py HOUR_CHOICES. */
val AVAILABLE_HOUR_CHOICES: List<Pair<Int, String>> = (6..19).map { it to String.format("%02d:00", it) }

// ── Become a provider ────────────────────────────────────────────────────

data class ProviderApplicationRequest(
    val name: String,
    val category: String,
    val bio: String,
    val location: String,
    val phone: String
)

data class ProviderApplicationStatus(
    val has_applied: Boolean,
    val is_active: Boolean? = null,
    val provider: ProviderProfile? = null
)

data class ProviderApplicationResponse(
    val detail: String,
    val provider: ProviderProfile
)
