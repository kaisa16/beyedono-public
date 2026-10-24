// file: app/src/main/java/ru/beyedono/app/domain/util/PriceFormatter.kt
package ru.beyedono.app.domain.util

import java.text.NumberFormat
import java.util.Locale

object PriceFormatter {
    private val format = NumberFormat.getCurrencyInstance(Locale("ru", "RU")).apply {
        maximumFractionDigits = 0
    }

    fun format(price: Double): String {
        return format.format(price).replace("RUB", "₽").replace("руб.", "₽")
    }
}