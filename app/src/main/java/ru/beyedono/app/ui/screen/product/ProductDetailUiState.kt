// file: app/src/main/java/ru/beyedono/app/ui/screen/product/ProductDetailUiState.kt
package ru.beyedono.app.ui.screen.product

import ru.beyedono.app.domain.model.Product

data class ProductDetailUiState(
    val product: Product? = null,
    val quantity: Int = 1,
    val isLoading: Boolean = true,
    val error: String? = null
)