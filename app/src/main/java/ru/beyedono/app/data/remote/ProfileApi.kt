// file: app/src/main/java/ru/beyedono/app/data/remote/ProfileApi.kt
package ru.beyedono.app.data.remote

import retrofit2.http.Body
import retrofit2.http.PUT
import ru.beyedono.app.data.remote.dto.ProfileUpdateRequest
import ru.beyedono.app.data.remote.dto.UserDto

interface ProfileApi {
    @PUT("profile")
    suspend fun updateProfile(@Body request: ProfileUpdateRequest): UserDto
}
