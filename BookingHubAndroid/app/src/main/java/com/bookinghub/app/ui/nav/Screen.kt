package com.bookinghub.app.ui.nav

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Home : Screen("home")
    data object MyBookings : Screen("my_bookings")

    data object ProviderList : Screen("providers/{category}/{categoryLabel}") {
        fun build(category: String, categoryLabel: String) =
            "providers/$category/${java.net.URLEncoder.encode(categoryLabel, "UTF-8")}"
    }

    data object ProviderDetail : Screen("provider/{providerId}") {
        fun build(providerId: Int) = "provider/$providerId"
    }

    data object Booking : Screen("booking/{providerId}") {
        fun build(providerId: Int) = "booking/$providerId"
    }

    data object Payment : Screen("payment/{bookingId}/{checkoutUrl}") {
        fun build(bookingId: Int, checkoutUrl: String) =
            "payment/$bookingId/${java.net.URLEncoder.encode(checkoutUrl, "UTF-8")}"
    }

    data object BookingSuccess : Screen("booking_success/{bookingId}") {
        fun build(bookingId: Int) = "booking_success/$bookingId"
    }

    data object BecomeProvider : Screen("become_provider")

    // Provider portal — only reachable from Home when the logged-in user owns a Provider
    data object ProviderDashboard : Screen("provider_portal/dashboard")
    data object ProviderPortalBookings : Screen("provider_portal/bookings")
    data object ProviderAvailability : Screen("provider_portal/availability")
    data object ProviderProfile : Screen("provider_portal/profile")
    data object ProviderEarnings : Screen("provider_portal/earnings")
}
