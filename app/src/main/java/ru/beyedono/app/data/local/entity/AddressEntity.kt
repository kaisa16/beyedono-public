// file: app/src/main/java/ru/beyedono/app/data/local/entity/AddressEntity.kt
package ru.beyedono.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "addresses")
data class AddressEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val city: String,
    val street: String,
    val house: String,
    val apartment: String?,
    val entrance: String?,
    val floor: String?,
    val intercom: String?,
    val lat: Double?,
    val lng: Double?,
    val isDefault: Boolean
)