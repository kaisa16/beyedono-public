// file: app/src/main/java/ru/beyedono/app/data/remote/dto/ProductDto.kt
package ru.beyedono.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(
    val id: String,
    val name: String,
    @SerialName("sort_order") val sortOrder: Int
)

@Serializable
data class ProductDto(
    val id: String,
    @SerialName("category_id") val categoryId: String,
    val name: String,
    val description: String? = null,
    val price: Double,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("is_available") val isAvailable: Boolean,
    @SerialName("is_spicy") val isSpicy: Boolean,
    @SerialName("is_vegetarian") val isVegetarian: Boolean
)

@Serializable
data class MenuResponse(
    val categories: List<CategoryDto>,
    val products: List<ProductDto>
)