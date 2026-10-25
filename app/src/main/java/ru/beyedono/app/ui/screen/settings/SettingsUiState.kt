// file: app/src/main/java/ru/beyedono/app/ui/screen/settings/SettingsUiState.kt
package ru.beyedono.app.ui.screen.settings

data class SettingsUiState(
    val isPushEnabled: Boolean = true,
    val theme: String = "system", // "system", "light", "dark"
    val language: String = "Русский",
    val showPinDialog: Boolean = false,
    val pinInput: String = "",
    val pinError: Boolean = false
)
