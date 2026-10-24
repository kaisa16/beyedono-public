// file: app/src/main/java/ru/beyedono/app/ui/screen/auth/ForgotPasswordUiState.kt
package ru.beyedono.app.ui.screen.auth

data class ForgotPasswordUiState(
    val email: String = "",
    val emailError: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val generalError: String? = null
)