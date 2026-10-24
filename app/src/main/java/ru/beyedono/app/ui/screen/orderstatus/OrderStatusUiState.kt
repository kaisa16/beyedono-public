// file: app/src/main/java/ru/beyedono/app/ui/screen/orderstatus/OrderStatusUiState.kt
package ru.beyedono.app.ui.screen.orderstatus

import ru.beyedono.app.data.local.entity.OrderEntity

data class OrderStatusUiState(
    val order: OrderEntity? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)