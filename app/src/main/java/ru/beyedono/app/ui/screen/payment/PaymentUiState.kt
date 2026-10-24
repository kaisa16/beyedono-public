// file: app/src/main/java/ru/beyedono/app/ui/screen/payment/PaymentUiState.kt
package ru.beyedono.app.ui.screen.payment

data class PaymentUiState(
    val orderId: String = "",
    val amount: Double = 0.0,
    val confirmationUrl: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)