// file: app/src/main/java/ru/beyedono/app/ui/screen/auth/RegisterViewModel.kt
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
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onNameChange(name: String) = _uiState.update { it.copy(name = name, nameError = null) }
    fun onEmailChange(email: String) = _uiState.update { it.copy(email = email, emailError = null) }
    fun onPasswordChange(password: String) = _uiState.update { it.copy(password = password, passwordError = null) }
    fun onConfirmPasswordChange(confirm: String) = _uiState.update { it.copy(confirmPassword = confirm, confirmPasswordError = null) }
    fun onAgreeChange(isAgreed: Boolean) = _uiState.update { it.copy(isAgreed = isAgreed, agreeError = null) }

    fun register() {
        val state = _uiState.value
        var isValid = true

        if (!Validators.isValidName(state.name)) {
            _uiState.update { it.copy(nameError = "Слишком короткое имя") }
            isValid = false
        }
        if (!Validators.isValidEmail(state.email)) {
            _uiState.update { it.copy(emailError = "Некорректный email") }
            isValid = false
        }
        if (!Validators.isValidPassword(state.password)) {
            _uiState.update { it.copy(passwordError = "Минимум 6 символов") }
            isValid = false
        }
        if (state.password != state.confirmPassword) {
            _uiState.update { it.copy(confirmPasswordError = "Пароли не совпадают") }
            isValid = false
        }
        if (!state.isAgreed) {
            _uiState.update { it.copy(agreeError = "Необходимо согласие") }
            isValid = false
        }

        if (!isValid) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = authRepository.register(state.name, state.email, state.password)
            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, generalError = error.message ?: "Ошибка регистрации") }
            }
        }
    }
}