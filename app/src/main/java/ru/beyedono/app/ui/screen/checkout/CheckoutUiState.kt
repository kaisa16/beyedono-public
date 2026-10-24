// file: app/src/main/java/ru/beyedono/app/ui/screen/checkout/CheckoutUiState.kt
package ru.beyedono.app.ui.screen.checkout

import ru.beyedono.app.data.local.entity.AddressEntity
import ru.beyedono.app.domain.model.CartItem

data class CheckoutUiState(
    val cartItems: List<CartItem> = emptyList(),
    val savedAddresses: List<AddressEntity> = emptyList(), // <--- НОВОЕ
    val subtotal: Double = 0.0,
    val deliveryPrice: Double = 0.0,
    val discount: Double = 0.0,
    val total: Double = 0.0,
    val city: String = "Якутск",
    val street: String = "",
    val house: String = "",
    val apartment: String = "",
    val entrance: String = "",
    val floor: String = "",
    val intercom: String = "",
    val note: String = "",
    val paymentMethod: String = "SBP",
    val isLoading: Boolean = false,
    val error: String? = null,
    val createdOrderId: String? = null
)
