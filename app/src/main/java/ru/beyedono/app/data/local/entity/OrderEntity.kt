// file: app/src/main/java/ru/beyedono/app/data/local/entity/OrderEntity.kt
package ru.beyedono.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val addressId: String?,
    val addressText: String?,
    val isPaid: Boolean,
    val status: String,
    val totalPrice: Double,
    val deliveryPrice: Double,
    val discount: Double,
    val note: String?,
    val adminNote: String?,
    val paymentMethod: String, // <--- НОВОЕ
    val itemsSummary: String,  // <--- НОВОЕ (состав заказа текстом)
    val createdAt: Long,
    val deliveryTime: Long?
)
