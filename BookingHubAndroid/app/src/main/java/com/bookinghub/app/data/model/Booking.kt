package com.bookinghub.app.data.model

data class BookingCreateRequest(
    val provider: Int,
    val service: Int?,
    val timeslot: Int?,
    val address: String,
    val notes: String
)

data class Booking(
    val id: Int,
    val provider: Int,
    val provider_name: String,
    val provider_photo: String?,
    val service: Int?,
    val service_name: String?,
    val price: String?,
    val timeslot: Int?,
    val start_time: String?,
    val address: String,
    val notes: String,
    val status: String,
    val status_label: String,
    val paystack_ref: String,
    val amount_paid: String?,
    val payment_verified: Boolean,
    val created_at: String
)

data class InitiatePaymentResponse(
    val authorization_url: String,
    val reference: String
)

data class VerifyPaymentRequest(
    val reference: String
)
