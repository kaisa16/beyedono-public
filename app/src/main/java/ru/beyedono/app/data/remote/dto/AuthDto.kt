// file: app/src/main/java/ru/beyedono/app/data/remote/dto/AuthDto.kt
package ru.beyedono.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable data class LoginRequest(val email: String, val password: String)
@Serializable data class RegisterRequest(val name: String, val email: String, val password: String)
@Serializable data class AuthResponse(@SerialName("access_token") val accessToken: String, @SerialName("refresh_token") val refreshToken: String, val user: UserDto)
@Serializable data class UserDto(val id: String, val name: String, val email: String, val phone: String? = null, @SerialName("avatar_url") val avatarUrl: String? = null)

@Serializable
data class ProfileUpdateRequest(
    val email: String, // <--- ДОБАВЛЕНО
    val name: String,
    val phone: String? = null
)
