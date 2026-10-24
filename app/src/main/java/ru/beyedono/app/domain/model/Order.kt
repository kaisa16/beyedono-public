// file: app/src/main/java/ru/beyedono/app/domain/model/Order.kt
package ru.beyedono.app.domain.model

data class Order(
    val id: String,
    val userId: String,
    val addressId: String?,
    val status: OrderStatus,
    val totalPrice: Double,
    val deliveryPrice: Double,
    val discount: Double,
    val note: String?,
    val createdAt: Long,
    val deliveryTime: Long?,
    val items: List<OrderItem>
)