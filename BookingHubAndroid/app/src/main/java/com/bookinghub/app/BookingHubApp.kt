package com.bookinghub.app

import android.app.Application
import com.bookinghub.app.data.remote.RetrofitClient
import com.bookinghub.app.data.remote.SessionManager
import com.bookinghub.app.data.repository.AuthRepository
import com.bookinghub.app.data.repository.BookingRepository
import com.bookinghub.app.data.repository.CatalogRepository
import com.bookinghub.app.data.repository.ProviderPortalRepository

/**
 * No Hilt/Dagger here on purpose — the app is small enough that a single hand-rolled
 * container is easier to read and modify. Everything downstream (ViewModels) pulls its
 * dependencies from `(application as BookingHubApp).container`.
 */
class AppContainer(app: Application) {
    val sessionManager = SessionManager(app.applicationContext)
    private val apiService = RetrofitClient.create(sessionManager)

    val authRepository = AuthRepository(apiService, sessionManager)
    val catalogRepository = CatalogRepository(apiService)
    val bookingRepository = BookingRepository(apiService)
    val providerPortalRepository = ProviderPortalRepository(apiService)
}

class BookingHubApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
