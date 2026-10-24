// file: app/src/main/java/ru/beyedono/app/domain/model/OrderStatus.kt
package ru.beyedono.app.domain.model

enum class OrderStatus {
    PENDING, COOKING, DELIVERING, DELIVERED, CANCELLED;

    fun getDisplayName(): String {
        return when (this) {
            PENDING -> "В ожидании"
            COOKING -> "Готовится"
            DELIVERING -> "В пути"
            DELIVERED -> "Доставлен"
            CANCELLED -> "Отменен"
        }
    }
}
