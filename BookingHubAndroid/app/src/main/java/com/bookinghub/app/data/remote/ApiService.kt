package com.bookinghub.app.data.remote

import com.bookinghub.app.data.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ── Auth ─────────────────────────────────────────────────────────────
    @POST("auth/register/")
    suspend fun register(@Body body: RegisterRequest): Response<AuthResponse>

    @POST("auth/login/")
    suspend fun login(@Body body: LoginRequest): Response<AuthResponse>

    @GET("auth/me/")
    suspend fun me(): Response<User>

    // ── Catalog ──────────────────────────────────────────────────────────
    @GET("categories/")
    suspend fun getCategories(): Response<List<Category>>

    @GET("providers/")
    suspend fun getProviders(
        @Query("category") category: String? = null,
        @Query("q") query: String? = null,
        @Query("availability") availability: String? = null,
        @Query("min_rating") minRating: String? = null,
        @Query("sort") sort: String? = null
    ): Response<List<ProviderSummary>>

    @GET("providers/{id}/")
    suspend fun getProviderDetail(@Path("id") providerId: Int): Response<ProviderDetail>

    @POST("providers/{id}/reviews/")
    suspend fun postReview(@Path("id") providerId: Int, @Body body: ReviewRequest): Response<Review>

    // ── Bookings ─────────────────────────────────────────────────────────
    @POST("bookings/")
    suspend fun createBooking(@Body body: BookingCreateRequest): Response<Booking>

    @GET("bookings/my/")
    suspend fun getMyBookings(): Response<List<Booking>>

    @GET("bookings/{id}/")
    suspend fun getBooking(@Path("id") bookingId: Int): Response<Booking>

    @POST("bookings/{id}/pay/")
    suspend fun initiatePayment(@Path("id") bookingId: Int): Response<InitiatePaymentResponse>

    @POST("bookings/{id}/verify/")
    suspend fun verifyPayment(@Path("id") bookingId: Int, @Body body: VerifyPaymentRequest): Response<Booking>

    // ── Become a provider ────────────────────────────────────────────────
    @GET("provider/apply/")
    suspend fun getApplicationStatus(): Response<ProviderApplicationStatus>

    @POST("provider/apply/")
    suspend fun applyToBecomeProvider(@Body body: ProviderApplicationRequest): Response<ProviderApplicationResponse>

    // ── Provider portal ─────────────────────────────────────────────────
    @GET("provider/portal/dashboard/")
    suspend fun getProviderDashboard(): Response<ProviderDashboard>

    @POST("provider/portal/status/")
    suspend fun toggleProviderStatus(): Response<ProviderProfile>

    @GET("provider/portal/bookings/")
    suspend fun getProviderBookings(@Query("status") status: String? = null): Response<List<CustomerBooking>>

    @POST("provider/portal/bookings/{id}/action/")
    suspend fun actOnBooking(@Path("id") bookingId: Int, @Body body: BookingActionRequest): Response<CustomerBooking>

    @GET("provider/portal/availability/")
    suspend fun getProviderAvailability(): Response<List<TimeSlot>>

    @POST("provider/portal/availability/")
    suspend fun addTimeSlots(@Body body: AddTimeSlotsRequest): Response<AddTimeSlotsResponse>

    @DELETE("provider/portal/availability/{id}/")
    suspend fun deleteTimeSlot(@Path("id") slotId: Int): Response<Unit>

    @GET("provider/portal/profile/")
    suspend fun getProviderProfile(): Response<ProviderProfile>

    @Multipart
    @PATCH("provider/portal/profile/")
    suspend fun updateProviderProfile(
        @Part("name") name: RequestBody? = null,
        @Part("category") category: RequestBody? = null,
        @Part("bio") bio: RequestBody? = null,
        @Part("location") location: RequestBody? = null,
        @Part("phone") phone: RequestBody? = null,
        @Part photo: MultipartBody.Part? = null
    ): Response<ProviderProfile>

    @GET("provider/portal/earnings/")
    suspend fun getProviderEarnings(): Response<ProviderEarnings>
}
