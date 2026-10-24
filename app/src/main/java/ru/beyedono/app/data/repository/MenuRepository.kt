// file: app/src/main/java/ru/beyedono/app/data/repository/MenuRepository.kt
package ru.beyedono.app.data.repository

import kotlinx.coroutines.flow.Flow
import ru.beyedono.app.data.local.dao.ProductDao
import ru.beyedono.app.data.local.entity.ProductEntity
import ru.beyedono.app.data.remote.MenuApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MenuRepository @Inject constructor(
    private val menuApi: MenuApi,
    private val productDao: ProductDao
) {
    fun getLocalProducts(): Flow<List<ProductEntity>> = productDao.getAllProducts()

    suspend fun syncMenu(): Result<Unit> = runCatching {
        val response = menuApi.getMenu()
        val entities = response.products.map {
            ProductEntity(
                id = it.id,
                categoryId = it.categoryId,
                name = it.name,
                description = it.description,
                price = it.price,
                imageUrl = it.imageUrl,
                isAvailable = it.isAvailable,
                isSpicy = it.isSpicy,
                isVegetarian = it.isVegetarian
            )
        }
        productDao.clearProducts()
        productDao.insertProducts(entities)
    }
}
