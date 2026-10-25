// file: app/src/main/java/ru/beyedono/app/ui/navigation/BeyedonoNavHost.kt
package ru.beyedono.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ru.beyedono.app.ui.screen.about.AboutCafeScreen
import ru.beyedono.app.ui.screen.addresses.AddressListScreen
import ru.beyedono.app.ui.screen.admin.AdminOrdersScreen
import ru.beyedono.app.ui.screen.auth.ForgotPasswordScreen
import ru.beyedono.app.ui.screen.auth.LoginScreen
import ru.beyedono.app.ui.screen.auth.RegisterScreen
import ru.beyedono.app.ui.screen.cart.CartScreen
import ru.beyedono.app.ui.screen.checkout.AddressPickerScreen
import ru.beyedono.app.ui.screen.checkout.CheckoutScreen
import ru.beyedono.app.ui.screen.favorites.FavoriteScreen
import ru.beyedono.app.ui.screen.history.OrderHistoryScreen
import ru.beyedono.app.ui.screen.menu.MenuScreen
import ru.beyedono.app.ui.screen.onboarding.OnboardingScreen
import ru.beyedono.app.ui.screen.orderstatus.OrderStatusScreen
import ru.beyedono.app.ui.screen.payment.PaymentScreen
import ru.beyedono.app.ui.screen.product.ProductDetailScreen
import ru.beyedono.app.ui.screen.profile.ProfileScreen
import ru.beyedono.app.ui.screen.settings.SettingsScreen
import ru.beyedono.app.ui.screen.splash.SplashScreen

@Composable
fun BeyedonoNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Routes.SPLASH) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onNavigateToOnboarding = { navController.navigate(Routes.ONBOARDING) { popUpTo(Routes.SPLASH) { inclusive = true } } },
                onNavigateToMenu = { navController.navigate(Routes.MENU) { popUpTo(Routes.SPLASH) { inclusive = true } } }
            )
        }
        composable(Routes.ONBOARDING) { OnboardingScreen(onFinish = { navController.navigate(Routes.LOGIN) { popUpTo(Routes.ONBOARDING) { inclusive = true } } }) }
        composable(Routes.LOGIN) {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) },
                onNavigateToForgotPassword = { navController.navigate(Routes.FORGOT_PASSWORD) },
                onLoginSuccess = { navController.navigate(Routes.MENU) { popUpTo(Routes.LOGIN) { inclusive = true } } }
            )
        }
        composable(Routes.REGISTER) {
            RegisterScreen(onNavigateToLogin = { navController.popBackStack() }, onRegisterSuccess = { navController.navigate(Routes.MENU) { popUpTo(Routes.LOGIN) { inclusive = true } } })
        }
        composable(Routes.FORGOT_PASSWORD) { ForgotPasswordScreen(onNavigateBack = { navController.popBackStack() }) }
        composable(Routes.MENU) {
            MenuScreen(onProductClick = { id -> navController.navigate(Routes.productDetail(id)) }, onCartClick = { navController.navigate(Routes.CART) }, onProfileClick = { navController.navigate(Routes.PROFILE) })
        }
        composable(route = Routes.PRODUCT_DETAIL, arguments = listOf(navArgument("productId") { type = NavType.StringType })) {
            ProductDetailScreen(onNavigateBack = { navController.popBackStack() }, onCartClick = { navController.navigate(Routes.CART) })
        }
        composable(Routes.CART) {
            CartScreen(onNavigateBack = { navController.popBackStack() }, onCheckoutClick = { discount -> navController.navigate(Routes.checkout(discount)) })
        }
        composable(route = Routes.CHECKOUT, arguments = listOf(navArgument("discount") { type = NavType.StringType })) {
            CheckoutScreen(
                onNavigateBack = { navController.popBackStack() }, 
                onNavigateToMap = { navController.navigate(Routes.ADDRESS_PICKER) },
                onNavigateToLogin = { navController.navigate(Routes.LOGIN) { popUpTo(Routes.MENU) } }, // <--- ПЕРЕХОД НА ЛОГИН
                onOrderPlaced = { orderId -> navController.navigate(Routes.payment(orderId)) { popUpTo(Routes.MENU) { inclusive = false } } }
            )
        }
        composable(Routes.ADDRESS_PICKER) {
            AddressPickerScreen(
                onNavigateBack = { navController.popBackStack() },
                onAddressSelected = { address, lat, lng ->
                    navController.previousBackStackEntry?.savedStateHandle?.set("picked_address", address)
                    navController.previousBackStackEntry?.savedStateHandle?.set("picked_lat", lat)
                    navController.previousBackStackEntry?.savedStateHandle?.set("picked_lng", lng)
                    navController.popBackStack()
                }
            )
        }
        composable(route = Routes.PAYMENT, arguments = listOf(navArgument("orderId") { type = NavType.StringType })) {
            PaymentScreen(
                // Если нажмут "Назад" на оплате - кидаем в меню, чтобы не было пустой корзины
                onNavigateBack = { navController.navigate(Routes.MENU) { popUpTo(Routes.MENU) { inclusive = true } } }, 
                onPaymentSuccess = { orderId -> navController.navigate(Routes.orderStatus(orderId)) { popUpTo(Routes.MENU) { inclusive = false } } }
            )
        }
        composable(route = Routes.ORDER_STATUS, arguments = listOf(navArgument("orderId") { type = NavType.StringType })) {
            OrderStatusScreen(onNavigateBack = { navController.navigate(Routes.MENU) { popUpTo(Routes.MENU) { inclusive = true } } })
        }
        composable(Routes.HISTORY) {
            OrderHistoryScreen(
                onNavigateBack = { navController.popBackStack() },
                onOrderClick = { orderId -> navController.navigate(Routes.orderStatus(orderId)) },
                onGoToCart = { navController.navigate(Routes.CART) { popUpTo(Routes.PROFILE) { inclusive = false } } }
            )
        }
        composable(Routes.PROFILE) {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHistory = { navController.navigate(Routes.HISTORY) },
                onNavigateToSettings = { navController.navigate(Routes.SETTINGS) },
                onNavigateToAddresses = { navController.navigate(Routes.MY_ADDRESSES) },
                onNavigateToAbout = { navController.navigate(Routes.ABOUT_CAFE) },
                onNavigateToFavorites = { navController.navigate(Routes.FAVORITES) },
                onLogout = { navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } } }
            )
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(onNavigateBack = { navController.popBackStack() }, onNavigateToAdmin = { navController.navigate(Routes.ADMIN_ORDERS) })
        }
        composable(Routes.ADMIN_ORDERS) { AdminOrdersScreen(onNavigateBack = { navController.popBackStack() }) }
        composable(Routes.MY_ADDRESSES) { AddressListScreen(onNavigateBack = { navController.popBackStack() }) }
        composable(Routes.ABOUT_CAFE) { AboutCafeScreen(onNavigateBack = { navController.popBackStack() }) }
        composable(Routes.FAVORITES) {
            FavoriteScreen(onNavigateBack = { navController.popBackStack() }, onProductClick = { id -> navController.navigate(Routes.productDetail(id)) })
        }
    }
}
