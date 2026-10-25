// file: app/src/main/java/ru/beyedono/app/data/repository/AdminRepository.kt
package ru.beyedono.app.data.repository

import ru.beyedono.app.data.remote.AdminApi
import ru.beyedono.app.data.remote.ProductCreateRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminRepository @Inject constructor(
    private val adminApi: AdminApi,
    private val menuRepository: MenuRepository
) {
    suspend fun addProduct(request: ProductCreateRequest): Result<Unit> = runCatching {
        adminApi.createProduct(request)
        menuRepository.syncMenu()
    }

    suspend fun updateProduct(id: String, request: ProductCreateRequest): Result<Unit> = runCatching {
        adminApi.updateProduct(id, request)
        menuRepository.syncMenu()
    }

    suspend fun deleteProduct(id: String): Result<Unit> = runCatching {
        adminApi.deleteProduct(id)
        menuRepository.syncMenu()
    }
}
