// file: app/src/main/java/ru/beyedono/app/data/remote/dto/PaymentDto.kt
package ru.beyedono.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaymentRequest(
    @SerialName("order_id") val orderId: String,
    val amount: Double,
    @SerialName("return_url") val returnUrl: String
)

@Serializable
data class PaymentResponse(
    val id: String,
    val status: String,
    @SerialName("confirmation_url") val confirmationUrl: String
)