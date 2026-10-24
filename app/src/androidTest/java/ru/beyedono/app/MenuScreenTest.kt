// file: app/src/androidTest/java/ru/beyedono/app/MenuScreenTest.kt
package ru.beyedono.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test
import ru.beyedono.app.ui.components.EmptyState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart

class MenuScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun emptyStateDisplayed() {
        composeTestRule.setContent {
            EmptyState(
                icon = Icons.Default.ShoppingCart,
                title = "Корзина пуста",
                description = "Добавьте товары"
            )
        }
        composeTestRule.onNodeWithText("Корзина пуста").assertIsDisplayed()
    }
}