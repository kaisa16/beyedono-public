// file: app/src/main/java/ru/beyedono/app/ui/screen/menu/MenuUiState.kt
package ru.beyedono.app.ui.screen.menu

import ru.beyedono.app.domain.model.Product

data class MenuUiState(
    val categories: List<String> = listOf(
        "Все", 
        "Основные блюда", 
        "Салаты", 
        "Фаст-Фуд", 
        "Гамбургеры", 
        "Чизбургеры", 
        "Закуски", 
        "Соусы", 
        "Пиццы", 
        "Роллы", 
        "Напитки"
    ),
    val selectedCategory: String = "Все",
    val searchQuery: String = "",
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)