// file: app/src/main/java/ru/beyedono/app/data/repository/AuthRepository.kt
package ru.beyedono.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import ru.beyedono.app.data.local.dao.AddressDao
import ru.beyedono.app.data.local.dao.CartDao
import ru.beyedono.app.data.local.dao.OrderDao
import ru.beyedono.app.data.local.dao.UserDao
import ru.beyedono.app.data.local.entity.UserEntity
import ru.beyedono.app.data.remote.AuthApi
import ru.beyedono.app.data.remote.FcmTokenRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val userDao: UserDao,
    private val cartDao: CartDao,
    private val orderDao: OrderDao,
    private val addressDao: AddressDao,
    private val favoritesRepository: FavoritesRepository,
    private val firebaseAuth: FirebaseAuth
) {
    suspend fun login(email: String, password: String): Result<Unit> = runCatching {
        val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        val firebaseUser = authResult.user ?: throw Exception("Ошибка входа")
        
        val user = UserEntity(
            id = firebaseUser.uid, name = firebaseUser.displayName ?: "Пользователь",
            email = email, phone = null, avatarUrl = null, createdAt = System.currentTimeMillis()
        )
        userDao.insertUser(user)
        updateFcmToken(email)
    }

    suspend fun register(name: String, email: String, password: String): Result<Unit> = runCatching {
        val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        val firebaseUser = authResult.user ?: throw Exception("Ошибка регистрации")
        
        val user = UserEntity(
            id = firebaseUser.uid, name = name, email = email,
            phone = null, avatarUrl = null, createdAt = System.currentTimeMillis()
        )
        userDao.insertUser(user)
        updateFcmToken(email)
    }

    // Этот метод мы использовали при входе/регистрации
    suspend fun updateFcmToken(email: String) {
        try {
            val token = FirebaseMessaging.getInstance().token.await()
            authApi.saveFcmToken(FcmTokenRequest(email, token))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // НОВЫЙ МЕТОД: Отправляет именно тот токен, который пришел от Firebase
    suspend fun sendFcmTokenToServer(email: String, token: String) {
        try {
            authApi.saveFcmToken(FcmTokenRequest(email, token))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun logout() {
        firebaseAuth.signOut()
        userDao.clearUser()
        cartDao.clearCart()
        orderDao.clearOrders()
        addressDao.clearAddresses()
        favoritesRepository.clearFavorites()
    }

    suspend fun deleteAccount(): Result<Unit> = runCatching {
        val user = firebaseAuth.currentUser
        user?.delete()?.await()
        logout()
    }
}
