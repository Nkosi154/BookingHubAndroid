package com.bookinghub.app.data.repository

import com.bookinghub.app.data.model.Booking
import com.bookinghub.app.data.model.BookingCreateRequest
import com.bookinghub.app.data.model.InitiatePaymentResponse
import com.bookinghub.app.data.model.VerifyPaymentRequest
import com.bookinghub.app.data.remote.ApiService
import com.bookinghub.app.data.remote.safeApiCall

class BookingRepository(private val api: ApiService) {

    suspend fun createBooking(
        providerId: Int,
        serviceId: Int?,
        timeslotId: Int?,
        address: String,
        notes: String
    ): Result<Booking> = safeApiCall {
        api.createBooking(BookingCreateRequest(providerId, serviceId, timeslotId, address, notes))
    }

    suspend fun getMyBookings(): Result<List<Booking>> =
        safeApiCall { api.getMyBookings() }

    suspend fun getBooking(bookingId: Int): Result<Booking> =
        safeApiCall { api.getBooking(bookingId) }

    suspend fun initiatePayment(bookingId: Int): Result<InitiatePaymentResponse> =
        safeApiCall { api.initiatePayment(bookingId) }

    suspend fun verifyPayment(bookingId: Int, reference: String): Result<Booking> =
        safeApiCall { api.verifyPayment(bookingId, VerifyPaymentRequest(reference)) }
}
