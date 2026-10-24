// file: app/src/main/java/ru/beyedono/app/data/repository/CartRepository.kt
package ru.beyedono.app.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.beyedono.app.data.local.dao.CartDao
import ru.beyedono.app.data.local.dao.ProductDao
import ru.beyedono.app.data.local.entity.CartItemEntity
import ru.beyedono.app.domain.model.CartItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepository @Inject constructor(
    private val cartDao: CartDao,
    private val productDao: ProductDao
) {
    fun getCartItems(): Flow<List<CartItem>> {
        return cartDao.getCartItems().map { items ->
            items.mapNotNull { entity ->
                val product = productDao.getProductById(entity.productId)
                product?.let {
                    CartItem(
                        productId = it.id,
                        name = it.name,
                        price = it.price,
                        imageUrl = it.imageUrl,
                        quantity = entity.quantity
                    )
                }
            }
        }
    }

    suspend fun addToCart(productId: String, price: Double) {
        val existing = cartDao.getCartItem(productId)
        val newQuantity = (existing?.quantity ?: 0) + 1
        cartDao.insertCartItem(CartItemEntity(productId, newQuantity, price))
    }

    suspend fun removeFromCart(productId: String) {
        val existing = cartDao.getCartItem(productId)
        if (existing != null) {
            if (existing.quantity > 1) {
                cartDao.insertCartItem(existing.copy(quantity = existing.quantity - 1))
            } else {
                cartDao.deleteCartItem(existing)
            }
        }
    }

    suspend fun clearCart() {
        cartDao.clearCart()
    }
}