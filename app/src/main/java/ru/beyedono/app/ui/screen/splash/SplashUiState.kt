// file: app/src/main/java/ru/beyedono/app/ui/screen/splash/SplashUiState.kt
package ru.beyedono.app.ui.screen.splash

sealed interface SplashUiState {
    data object Loading : SplashUiState
    data object NavigateToOnboarding : SplashUiState
    data object NavigateToMenu : SplashUiState
}