// file: app/src/main/java/ru/beyedono/app/ui/screen/admin/AdminOrdersUiState.kt
package ru.beyedono.app.ui.screen.admin

import ru.beyedono.app.data.local.entity.OrderEntity

data class AdminOrdersUiState(
    val orders: List<OrderEntity> = emptyList(),
    val isLoading: Boolean = true
)
