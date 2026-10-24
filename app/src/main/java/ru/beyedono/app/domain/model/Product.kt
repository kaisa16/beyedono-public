// file: app/src/main/java/ru/beyedono/app/domain/model/Product.kt
package ru.beyedono.app.domain.model

data class Product(
    val id: String,
    val categoryId: String,
    val name: String,
    val description: String?,
    val price: Double,
    val imageUrl: String?,
    val isAvailable: Boolean,
    val isSpicy: Boolean,
    val isVegetarian: Boolean
)