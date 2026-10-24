// file: app/src/test/java/ru/beyedono/app/CartRepositoryTest.kt
package ru.beyedono.app

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import ru.beyedono.app.data.local.dao.CartDao
import ru.beyedono.app.data.local.dao.ProductDao
import ru.beyedono.app.data.local.entity.CartItemEntity
import ru.beyedono.app.data.repository.CartRepository

class CartRepositoryTest {
    private val cartDao = mockk<CartDao>(relaxed = true)
    private val productDao = mockk<ProductDao>(relaxed = true)
    private val repository = CartRepository(cartDao, productDao)

    @Test
    fun `add to cart increases quantity`() = runTest {
        coEvery { cartDao.getCartItem("1") } returns CartItemEntity("1", 1, 100.0)
        
        repository.addToCart("1", 100.0)
        
        coVerify { cartDao.insertCartItem(CartItemEntity("1", 2, 100.0)) }
    }
}