// file: app/src/androidTest/java/ru/beyedono/app/CartScreenTest.kt
package ru.beyedono.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test
import ru.beyedono.app.ui.components.QuantitySelector

class CartScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun quantitySelectorDisplaysValue() {
        composeTestRule.setContent {
            QuantitySelector(quantity = 3, onDecrease = {}, onIncrease = {})
        }
        composeTestRule.onNodeWithText("3").assertIsDisplayed()
    }
}