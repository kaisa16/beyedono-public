// file: app/src/test/java/ru/beyedono/app/ValidatorsTest.kt
package ru.beyedono.app

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.beyedono.app.domain.util.Validators

class ValidatorsTest {
    @Test
    fun `valid email check`() {
        assertTrue(Validators.isValidEmail("test@example.com"))
        assertFalse(Validators.isValidEmail("test.com"))
        assertFalse(Validators.isValidEmail(""))
    }

    @Test
    fun `valid password check`() {
        assertTrue(Validators.isValidPassword("123456"))
        assertFalse(Validators.isValidPassword("12345"))
    }
}