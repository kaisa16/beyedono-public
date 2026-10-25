// file: app/src/main/java/ru/beyedono/app/data/remote/dto/OrderDto.kt
package ru.beyedono.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateOrderRequest(
    @SerialName("address_id") val addressId: String?,
    @SerialName("address_text") val addressText: String?,
    val items: List<OrderItemRequest>,
    val note: String? = null,
    @SerialName("delivery_time") val deliveryTime: Long? = null,
    @SerialName("payment_method") val paymentMethod: String,
    val discount: Double = 0.0,
    @SerialName("delivery_price") val deliveryPrice: Double = 0.0,
    @SerialName("user_email") val userEmail: String? = null,
    @SerialName("promo_code") val promoCode: String? = null
)

@Serializable
data class OrderItemRequest(
    @SerialName("product_id") val productId: String,
    val quantity: Int
)

@Serializable
data class StatusUpdateRequest(
    val status: String,
    @SerialName("admin_note") val adminNote: String? = null
)

@Serializable
data class OrderResponse(
    val id: String,
    @SerialName("address_text") val addressText: String? = null,
    @SerialName("is_paid") val isPaid: Boolean,
    val status: String,
    @SerialName("total_price") val totalPrice: Double,
    @SerialName("delivery_price") val deliveryPrice: Double,
    val discount: Double,
    val note: String? = null,
    @SerialName("admin_note") val adminNote: String? = null,
    @SerialName("payment_method") val paymentMethod: String,
    @SerialName("user_email") val userEmail: String? = null, // <--- ДОБАВЛЕНО
    @SerialName("user_phone") val userPhone: String? = null, // <--- ДОБАВЛЕНО
    @SerialName("created_at") val createdAt: String,
    @SerialName("delivery_time") val deliveryTime: String? = null,
    val items: List<OrderItemDto>
)

@Serializable
data class OrderItemDto(
    val id: String,
    @SerialName("product_id") val productId: String,
    val name: String,
    val quantity: Int,
    val price: Double
)
