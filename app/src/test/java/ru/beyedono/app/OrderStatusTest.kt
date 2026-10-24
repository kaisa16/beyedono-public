// file: app/src/test/java/ru/beyedono/app/OrderStatusTest.kt
package ru.beyedono.app

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.beyedono.app.domain.model.OrderStatus

class OrderStatusTest {
    @Test
    fun `display names are correct`() {
        assertEquals("В ожидании", OrderStatus.PENDING.getDisplayName())
        assertEquals("Готовится", OrderStatus.COOKING.getDisplayName())
        assertEquals("В пути", OrderStatus.DELIVERING.getDisplayName())
        assertEquals("Доставлен", OrderStatus.DELIVERED.getDisplayName())
    }
}