// file: app/src/main/java/ru/beyedono/app/domain/model/User.kt
package ru.beyedono.app.domain.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val phone: String? = null,
    val avatarUrl: String? = null
)