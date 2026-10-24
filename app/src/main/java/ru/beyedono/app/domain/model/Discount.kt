// file: app/src/main/java/ru/beyedono/app/domain/model/Discount.kt
package ru.beyedono.app.domain.model

data class Discount(
    val id: String,
    val code: String,
    val percent: Int,
    val validUntil: Long?,
    val description: String?
)