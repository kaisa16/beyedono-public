// file: app/src/main/java/ru/beyedono/app/ui/screen/cart/CartUiState.kt
package ru.beyedono.app.ui.screen.cart

import ru.beyedono.app.domain.model.CartItem

data class CartUiState(
    val items: List<CartItem> = emptyList(),
    val subtotal: Double = 0.0,
    val deliveryPrice: Double = 0.0,
    val total: Double = 0.0,
    val promoCode: String = "",
    val discount: Double = 0.0,
    val isLoading: Boolean = false,
    val error: String? = null
)