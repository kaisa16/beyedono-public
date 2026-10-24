// file: app/src/main/java/ru/beyedono/app/ui/screen/auth/LoginUiState.kt
package ru.beyedono.app.ui.screen.auth

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val generalError: String? = null,
    val isSuccess: Boolean = false
)