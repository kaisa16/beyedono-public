// file: app/src/main/java/ru/beyedono/app/data/local/entity/ProductEntity.kt
package ru.beyedono.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val categoryId: String,
    val name: String,
    val description: String?,
    val price: Double,
    val imageUrl: String?,
    val isAvailable: Boolean,
    val isSpicy: Boolean,
    val isVegetarian: Boolean
)