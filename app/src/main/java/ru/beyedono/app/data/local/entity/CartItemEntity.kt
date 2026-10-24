// file: app/src/main/java/ru/beyedono/app/data/local/entity/CartItemEntity.kt
package ru.beyedono.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val productId: String,
    val quantity: Int,
    val price: Double
)