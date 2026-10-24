// file: app/src/main/java/ru/beyedono/app/ui/navigation/Routes.kt
package ru.beyedono.app.ui.navigation

object Routes {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT_PASSWORD = "forgot_password"
    const val MENU = "menu"
    const val PRODUCT_DETAIL = "product_detail/{productId}"
    const val CART = "cart"
    const val CHECKOUT = "checkout?discount={discount}"
    const val ADDRESS_PICKER = "address_picker"
    const val PAYMENT = "payment/{orderId}"
    const val ORDER_STATUS = "order_status/{orderId}"
    const val HISTORY = "history"
    const val PROFILE = "profile"
    const val SETTINGS = "settings"
    const val ADMIN_ORDERS = "admin_orders"
    const val MY_ADDRESSES = "my_addresses" // <--- НОВЫЙ МАРШРУТ

    fun productDetail(productId: String) = "product_detail/$productId"
    fun payment(orderId: String) = "payment/$orderId"
    fun orderStatus(orderId: String) = "order_status/$orderId"
    fun checkout(discount: Double) = "checkout?discount=$discount"
}
