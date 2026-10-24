// file: app/src/main/java/ru/beyedono/app/ui/screen/profile/ProfileUiState.kt
package ru.beyedono.app.ui.screen.profile

import ru.beyedono.app.data.local.entity.UserEntity

data class ProfileUiState(
    val user: UserEntity? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)