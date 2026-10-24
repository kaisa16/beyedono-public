// file: app/src/test/java/ru/beyedono/app/PriceFormatterTest.kt
package ru.beyedono.app

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.beyedono.app.domain.util.PriceFormatter

class PriceFormatterTest {
    @Test
    fun `format price correctly`() {
        assertEquals("1 500 ₽", PriceFormatter.format(1500.0).replace("\u00A0", " "))
        assertEquals("0 ₽", PriceFormatter.format(0.0).replace("\u00A0", " "))
        assertEquals("450 ₽", PriceFormatter.format(450.0).replace("\u00A0", " "))
    }
}