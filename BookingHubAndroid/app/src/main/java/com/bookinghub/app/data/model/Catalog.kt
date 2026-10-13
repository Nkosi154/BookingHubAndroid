package com.bookinghub.app.data.model

data class Category(
    val key: String,
    val label: String
)

data class Service(
    val id: Int,
    val name: String,
    val description: String,
    val price: String,
    val duration_hours: Double
)

data class TimeSlot(
    val id: Int,
    val start_time: String,
    val is_booked: Boolean
)

data class Review(
    val id: Int,
    val username: String,
    val rating: Int,
    val comment: String,
    val created_at: String?
)

data class ReviewRequest(
    val rating: Int,
    val comment: String
)

/** Lightweight provider shape returned by the list endpoint (/api/providers/). */
data class ProviderSummary(
    val id: Int,
    val name: String,
    val category: String,
    val category_label: String,
    val location: String,
    val rating: Double,
    val availability_status: String,
    val photo_url: String?
)

/** Full provider shape returned by the detail endpoint (/api/providers/{id}/). */
data class ProviderDetail(
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
    val services: List<Service>,
    val timeslots: List<TimeSlot>,
    val reviews: List<Review>
)
