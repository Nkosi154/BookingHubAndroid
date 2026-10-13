package com.bookinghub.app.ui.nav

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bookinghub.app.AppContainer
import com.bookinghub.app.ui.auth.AuthViewModel
import com.bookinghub.app.ui.booking.BookingViewModel
import com.bookinghub.app.ui.home.HomeViewModel
import com.bookinghub.app.ui.mybookings.MyBookingsViewModel
import com.bookinghub.app.ui.provider.ProviderDetailViewModel
import com.bookinghub.app.ui.provider.ProviderListViewModel
import com.bookinghub.app.ui.providerportal.BecomeProviderViewModel
import com.bookinghub.app.ui.providerportal.ProviderAvailabilityViewModel
import com.bookinghub.app.ui.providerportal.ProviderBookingsViewModel
import com.bookinghub.app.ui.providerportal.ProviderDashboardViewModel
import com.bookinghub.app.ui.providerportal.ProviderEarningsViewModel
import com.bookinghub.app.ui.providerportal.ProviderProfileViewModel

class BookingHubViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) ->
                AuthViewModel(container.authRepository) as T

            modelClass.isAssignableFrom(HomeViewModel::class.java) ->
                HomeViewModel(container.catalogRepository, container.authRepository) as T

            modelClass.isAssignableFrom(ProviderListViewModel::class.java) ->
                ProviderListViewModel(container.catalogRepository) as T

            modelClass.isAssignableFrom(ProviderDetailViewModel::class.java) ->
                ProviderDetailViewModel(container.catalogRepository) as T

            modelClass.isAssignableFrom(BookingViewModel::class.java) ->
                BookingViewModel(container.catalogRepository, container.bookingRepository) as T

            modelClass.isAssignableFrom(MyBookingsViewModel::class.java) ->
                MyBookingsViewModel(container.bookingRepository) as T

            modelClass.isAssignableFrom(ProviderDashboardViewModel::class.java) ->
                ProviderDashboardViewModel(container.providerPortalRepository) as T

            modelClass.isAssignableFrom(ProviderBookingsViewModel::class.java) ->
                ProviderBookingsViewModel(container.providerPortalRepository) as T

            modelClass.isAssignableFrom(ProviderAvailabilityViewModel::class.java) ->
                ProviderAvailabilityViewModel(container.providerPortalRepository) as T

            modelClass.isAssignableFrom(ProviderProfileViewModel::class.java) ->
                ProviderProfileViewModel(container.providerPortalRepository) as T

            modelClass.isAssignableFrom(ProviderEarningsViewModel::class.java) ->
                ProviderEarningsViewModel(container.providerPortalRepository) as T

            modelClass.isAssignableFrom(BecomeProviderViewModel::class.java) ->
                BecomeProviderViewModel(container.providerPortalRepository, container.authRepository) as T

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
