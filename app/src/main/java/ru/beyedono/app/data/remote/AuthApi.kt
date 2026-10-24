// file: app/src/main/java/ru/beyedono/app/data/remote/AuthApi.kt
package ru.beyedono.app.data.remote

import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.POST
import ru.beyedono.app.data.remote.dto.AuthResponse
import ru.beyedono.app.data.remote.dto.LoginRequest
import ru.beyedono.app.data.remote.dto.RegisterRequest

@Serializable
data class FcmTokenRequest(val email: String, val fcm_token: String)

interface AuthApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("users/fcm-token")
    suspend fun saveFcmToken(@Body request: FcmTokenRequest)
}
