// file: app/src/main/java/ru/beyedono/app/domain/util/Result.kt
package ru.beyedono.app.domain.util

sealed interface Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>
    data class Error(val message: String, val throwable: Throwable? = null) : Resource<Nothing>
    data object Loading : Resource<Nothing>
}