// file: app/src/main/java/ru/beyedono/app/ui/screen/profile/ProfileViewModel.kt
package ru.beyedono.app.ui.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.beyedono.app.data.local.dao.UserDao
import ru.beyedono.app.data.repository.AuthRepository
import ru.beyedono.app.data.repository.ProfileRepository
import ru.beyedono.app.domain.util.Validators
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userDao: UserDao,
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            userDao.getUser().collect { user ->
                _uiState.update { it.copy(user = user, isLoading = false) }
            }
        }
    }

    fun updateProfile(name: String, phone: String, onSuccess: () -> Unit) {
        // ВАЛИДАЦИЯ
        if (name.isBlank() || name.length < 2) {
            _uiState.update { it.copy(error = "Слишком короткое имя") }
            return
        }
        if (phone.isNotBlank() && !Validators.isValidPhone(phone)) {
            _uiState.update { it.copy(error = "Некорректный номер телефона") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            profileRepository.updateProfile(name, phone)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    onSuccess()
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onLoggedOut()
        }
    }

    fun deleteAccount(onDeleted: () -> Unit) {
        viewModelScope.launch {
            authRepository.deleteAccount()
                .onSuccess { onDeleted() }
                .onFailure { error ->
                    _uiState.update { it.copy(error = error.message ?: "Ошибка удаления аккаунта") }
                }
        }
    }
}
