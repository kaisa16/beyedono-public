// file: app/src/main/java/ru/beyedono/app/domain/model/Payment.kt
package ru.beyedono.app.domain.model

data class Payment(
    val id: String,
    val orderId: String,
    val amount: Double,
    val status: String,
    val confirmationUrl: String?
)