// file: app/src/main/java/ru/beyedono/app/ui/screen/menu/MenuUiState.kt
package ru.beyedono.app.ui.screen.menu

import ru.beyedono.app.domain.model.Product

data class MenuUiState(
    val categories: List<String> = emptyList(),
    val selectedCategory: String = "Все",
    val searchQuery: String = "",
    val products: List<Product> = emptyList(),
    val favoriteIds: Set<String> = emptySet(),
    val showOnlySpicy: Boolean = false,
    val showOnlyVegetarian: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null
)