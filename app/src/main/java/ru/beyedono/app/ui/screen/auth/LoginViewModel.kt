// file: app/src/main/java/ru/beyedono/app/ui/screen/auth/LoginViewModel.kt
package ru.beyedono.app.ui.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.beyedono.app.data.repository.AuthRepository
import ru.beyedono.app.domain.util.Validators
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, emailError = null, generalError = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null, generalError = null) }
    }

    fun login() {
        val state = _uiState.value
        var isValid = true

        if (!Validators.isValidEmail(state.email)) {
            _uiState.update { it.copy(emailError = "Некорректный email") }
            isValid = false
        }
        if (!Validators.isValidPassword(state.password)) {
            _uiState.update { it.copy(passwordError = "Минимум 6 символов") }
            isValid = false
        }

        if (!isValid) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = authRepository.login(state.email, state.password)
            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, generalError = error.message ?: "Ошибка входа") }
            }
        }
    }
}