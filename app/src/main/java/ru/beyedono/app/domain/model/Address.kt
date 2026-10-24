// file: app/src/main/java/ru/beyedono/app/domain/model/Address.kt
package ru.beyedono.app.domain.model

data class Address(
    val id: String,
    val userId: String,
    val city: String,
    val street: String,
    val house: String,
    val apartment: String? = null,
    val entrance: String? = null,
    val floor: String? = null,
    val intercom: String? = null,
    val lat: Double? = null,
    val lng: Double? = null,
    val isDefault: Boolean = false
) {
    fun getFullAddress(): String {
        val apt = if (!apartment.isNullOrBlank()) ", кв. $apartment" else ""
        return "$city, ул. $street, д. $house$apt"
    }
}