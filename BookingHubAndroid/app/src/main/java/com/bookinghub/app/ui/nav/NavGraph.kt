package com.bookinghub.app.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bookinghub.app.AppContainer
import com.bookinghub.app.ui.auth.LoginScreen
import com.bookinghub.app.ui.auth.RegisterScreen
import com.bookinghub.app.ui.booking.BookingScreen
import com.bookinghub.app.ui.booking.BookingSuccessScreen
import com.bookinghub.app.ui.booking.PaymentWebViewScreen
import com.bookinghub.app.ui.home.HomeScreen
import com.bookinghub.app.ui.mybookings.MyBookingsScreen
import com.bookinghub.app.ui.provider.ProviderDetailScreen
import com.bookinghub.app.ui.provider.ProviderListScreen
import com.bookinghub.app.ui.providerportal.BecomeProviderScreen
import com.bookinghub.app.ui.providerportal.ProviderAvailabilityScreen
import com.bookinghub.app.ui.providerportal.ProviderBookingsScreen
import com.bookinghub.app.ui.providerportal.ProviderDashboardScreen
import com.bookinghub.app.ui.providerportal.ProviderEarningsScreen
import com.bookinghub.app.ui.providerportal.ProviderProfileScreen
import kotlinx.coroutines.flow.first
import java.net.URLDecoder

@Composable
fun BookingHubNavGraph(container: AppContainer) {
    val navController: NavHostController = rememberNavController()
    val factory = remember { BookingHubViewModelFactory(container) }

    var startDestination by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) {
        val loggedIn = container.sessionManager.tokenFlow.first() != null
        startDestination = if (loggedIn) Screen.Home.route else Screen.Login.route
    }
    val start = startDestination ?: return

    NavHost(navController = navController, startDestination = start) {

        composable(Screen.Login.route) {
            LoginScreen(
                factory = factory,
                onLoggedIn = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onGoToRegister = { navController.navigate(Screen.Register.route) }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                factory = factory,
                onRegistered = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onBackToLogin = { navController.popBackStack() }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                factory = factory,
                onCategoryClick = { category, label ->
                    navController.navigate(Screen.ProviderList.build(category, label))
                },
                onProviderClick = { id ->
                    navController.navigate(Screen.ProviderDetail.build(id))
                },
                onMyBookingsClick = { navController.navigate(Screen.MyBookings.route) },
                onProviderPortalClick = { navController.navigate(Screen.ProviderDashboard.route) },
                onBecomeProviderClick = { navController.navigate(Screen.BecomeProvider.route) },
                onLoggedOut = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0)
                    }
                }
            )
        }

        composable(
            route = Screen.ProviderList.route,
            arguments = listOf(
                navArgument("category") { type = NavType.StringType },
                navArgument("categoryLabel") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category").orEmpty()
            val labelEncoded = backStackEntry.arguments?.getString("categoryLabel").orEmpty()
            val label = URLDecoder.decode(labelEncoded, "UTF-8")
            ProviderListScreen(
                factory = factory,
                category = category,
                categoryLabel = label,
                onBack = { navController.popBackStack() },
                onProviderClick = { id -> navController.navigate(Screen.ProviderDetail.build(id)) }
            )
        }

        composable(
            route = Screen.ProviderDetail.route,
            arguments = listOf(navArgument("providerId") { type = NavType.IntType })
        ) { backStackEntry ->
            val providerId = backStackEntry.arguments?.getInt("providerId") ?: return@composable
            ProviderDetailScreen(
                factory = factory,
                providerId = providerId,
                onBack = { navController.popBackStack() },
                onBookClick = { id -> navController.navigate(Screen.Booking.build(id)) }
            )
        }

        composable(
            route = Screen.Booking.route,
            arguments = listOf(navArgument("providerId") { type = NavType.IntType })
        ) { backStackEntry ->
            val providerId = backStackEntry.arguments?.getInt("providerId") ?: return@composable
            BookingScreen(
                factory = factory,
                providerId = providerId,
                onBack = { navController.popBackStack() },
                onPaymentReady = { bookingId, checkoutUrl ->
                    navController.navigate(Screen.Payment.build(bookingId, checkoutUrl))
                }
            )
        }

        composable(
            route = Screen.Payment.route,
            arguments = listOf(
                navArgument("bookingId") { type = NavType.IntType },
                navArgument("checkoutUrl") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val bookingId = backStackEntry.arguments?.getInt("bookingId") ?: return@composable
            val urlEncoded = backStackEntry.arguments?.getString("checkoutUrl").orEmpty()
            val checkoutUrl = URLDecoder.decode(urlEncoded, "UTF-8")
            PaymentWebViewScreen(
                bookingId = bookingId,
                checkoutUrl = checkoutUrl,
                onVerified = { success ->
                    navController.navigate(Screen.BookingSuccess.build(if (success) bookingId else -1)) {
                        popUpTo(Screen.Home.route)
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.BookingSuccess.route,
            arguments = listOf(navArgument("bookingId") { type = NavType.IntType })
        ) { backStackEntry ->
            val bookingId = backStackEntry.arguments?.getInt("bookingId") ?: -1
            BookingSuccessScreen(
                success = bookingId != -1,
                onDone = {
                    navController.navigate(Screen.MyBookings.route) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }

        composable(Screen.MyBookings.route) {
            MyBookingsScreen(
                factory = factory,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.BecomeProvider.route) {
            BecomeProviderScreen(
                factory = factory,
                onBack = { navController.popBackStack() },
                onDone = {
                    navController.navigate(Screen.ProviderDashboard.route) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }

        // ── Provider portal ──────────────────────────────────────────────
        composable(Screen.ProviderDashboard.route) {
            ProviderDashboardScreen(
                factory = factory,
                onBack = { navController.popBackStack() },
                onBookingsClick = { navController.navigate(Screen.ProviderPortalBookings.route) },
                onAvailabilityClick = { navController.navigate(Screen.ProviderAvailability.route) },
                onProfileClick = { navController.navigate(Screen.ProviderProfile.route) },
                onEarningsClick = { navController.navigate(Screen.ProviderEarnings.route) }
            )
        }

        composable(Screen.ProviderPortalBookings.route) {
            ProviderBookingsScreen(factory = factory, onBack = { navController.popBackStack() })
        }

        composable(Screen.ProviderAvailability.route) {
            ProviderAvailabilityScreen(factory = factory, onBack = { navController.popBackStack() })
        }

        composable(Screen.ProviderProfile.route) {
            ProviderProfileScreen(factory = factory, onBack = { navController.popBackStack() })
        }

        composable(Screen.ProviderEarnings.route) {
            ProviderEarningsScreen(factory = factory, onBack = { navController.popBackStack() })
        }
    }
}
