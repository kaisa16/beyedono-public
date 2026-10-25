// file: app/src/main/java/ru/beyedono/app/ui/screen/admin_menu/AdminMenuUiState.kt
package ru.beyedono.app.ui.screen.admin_menu

import ru.beyedono.app.data.local.entity.ProductEntity

data class AdminMenuUiState(
    val products: List<ProductEntity> = emptyList(),
    val isLoading: Boolean = true,
    val showDialog: Boolean = false,
    val editingProduct: ProductEntity? = null, // Если null - добавление, иначе - редактирование
    val error: String? = null
)
