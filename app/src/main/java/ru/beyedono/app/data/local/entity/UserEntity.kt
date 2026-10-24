// file: app/src/main/java/ru/beyedono/app/data/local/entity/UserEntity.kt
package ru.beyedono.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val phone: String?,
    val avatarUrl: String?,
    val createdAt: Long
)