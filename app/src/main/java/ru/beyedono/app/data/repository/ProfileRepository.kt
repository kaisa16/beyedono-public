// file: app/src/main/java/ru/beyedono/app/data/repository/ProfileRepository.kt
package ru.beyedono.app.data.repository

import kotlinx.coroutines.flow.first
import ru.beyedono.app.data.local.dao.UserDao
import ru.beyedono.app.data.remote.ProfileApi
import ru.beyedono.app.data.remote.dto.ProfileUpdateRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val profileApi: ProfileApi,
    private val userDao: UserDao
) {
    suspend fun updateProfile(name: String, phone: String): Result<Unit> = runCatching {
        val currentUser = userDao.getUser().first() ?: throw Exception("Пользователь не найден")
        val updatedUser = profileApi.updateProfile(ProfileUpdateRequest(currentUser.email, name, phone))
        userDao.insertUser(currentUser.copy(name = updatedUser.name, phone = updatedUser.phone))
    }
}
