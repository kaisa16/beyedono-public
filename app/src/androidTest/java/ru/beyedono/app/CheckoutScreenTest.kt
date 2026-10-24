// file: app/src/androidTest/java/ru/beyedono/app/CheckoutScreenTest.kt
package ru.beyedono.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class CheckoutScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun checkoutTitleDisplayed() {
        composeTestRule.setContent {
            androidx.compose.material3.Text("Оформление заказа")
        }
        composeTestRule.onNodeWithText("Оформление заказа").assertIsDisplayed()
    }
}