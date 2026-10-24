// file: app/src/main/java/ru/beyedono/app/ui/screen/history/OrderHistoryUiState.kt
package ru.beyedono.app.ui.screen.history

import ru.beyedono.app.data.local.entity.OrderEntity

data class OrderHistoryUiState(
    val orders: List<OrderEntity> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)