// file: app/src/main/java/ru/beyedono/app/domain/model/OrderItem.kt
package ru.beyedono.app.domain.model

data class OrderItem(
    val id: String,
    val productId: String,
    val name: String,
    val quantity: Int,
    val price: Double
) {
    val totalPrice: Double get() = price * quantity
}