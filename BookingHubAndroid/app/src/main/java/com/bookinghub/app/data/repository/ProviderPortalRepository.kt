package com.bookinghub.app.data.repository

import com.bookinghub.app.data.model.*
import com.bookinghub.app.data.remote.ApiService
import com.bookinghub.app.data.remote.safeApiCall
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class ProviderPortalRepository(private val api: ApiService) {

    suspend fun getApplicationStatus(): Result<ProviderApplicationStatus> =
        safeApiCall { api.getApplicationStatus() }

    suspend fun applyToBecomeProvider(
        name: String, category: String, bio: String, location: String, phone: String
    ): Result<ProviderApplicationResponse> =
        safeApiCall { api.applyToBecomeProvider(ProviderApplicationRequest(name, category, bio, location, phone)) }

    suspend fun getDashboard(): Result<ProviderDashboard> =
        safeApiCall { api.getProviderDashboard() }

    suspend fun toggleStatus(): Result<ProviderProfile> =
        safeApiCall { api.toggleProviderStatus() }

    suspend fun getBookings(status: String? = null): Result<List<CustomerBooking>> =
        safeApiCall { api.getProviderBookings(status) }

    suspend fun actOnBooking(bookingId: Int, action: String): Result<CustomerBooking> =
        safeApiCall { api.actOnBooking(bookingId, BookingActionRequest(action)) }

    suspend fun getAvailability(): Result<List<TimeSlot>> =
        safeApiCall { api.getProviderAvailability() }

    suspend fun addTimeSlots(date: String, hours: List<Int>): Result<AddTimeSlotsResponse> =
        safeApiCall { api.addTimeSlots(AddTimeSlotsRequest(date, hours)) }

    suspend fun deleteTimeSlot(slotId: Int): Result<Unit> =
        safeApiCall { api.deleteTimeSlot(slotId) }

    suspend fun getProfile(): Result<ProviderProfile> =
        safeApiCall { api.getProviderProfile() }

    suspend fun updateProfile(
        name: String? = null,
        category: String? = null,
        bio: String? = null,
        location: String? = null,
        phone: String? = null,
        photoFile: File? = null
    ): Result<ProviderProfile> = safeApiCall {
        fun part(value: String?) = value?.toRequestBody("text/plain".toMediaTypeOrNull())
        val photoPart = photoFile?.let {
            MultipartBody.Part.createFormData(
                "photo", it.name, it.asRequestBody("image/*".toMediaTypeOrNull())
            )
        }
        api.updateProviderProfile(part(name), part(category), part(bio), part(location), part(phone), photoPart)
    }

    suspend fun getEarnings(): Result<ProviderEarnings> =
        safeApiCall { api.getProviderEarnings() }
}
