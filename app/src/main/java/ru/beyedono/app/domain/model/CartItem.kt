// file: app/src/main/java/ru/beyedono/app/domain/model/CartItem.kt
package ru.beyedono.app.domain.model

data class CartItem(
    val productId: String,
    val name: String,
    val price: Double,
    val imageUrl: String?,
    val quantity: Int
) {
    val totalPrice: Double get() = price * quantity
}