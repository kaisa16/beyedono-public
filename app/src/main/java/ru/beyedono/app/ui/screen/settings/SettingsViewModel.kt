// file: app/src/main/java/ru/beyedono/app/ui/screen/settings/SettingsViewModel.kt
package ru.beyedono.app.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.beyedono.app.data.repository.SettingsRepository
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val ADMIN_PIN = "1234"

    init {
        viewModelScope.launch {
            settingsRepository.theme.collect { theme ->
                _uiState.update { it.copy(theme = theme) }
            }
        }
    }

    fun togglePush(enabled: Boolean) = _uiState.update { it.copy(isPushEnabled = enabled) }
    
    fun setTheme(theme: String) {
        _uiState.update { it.copy(theme = theme) }
        viewModelScope.launch {
            settingsRepository.setTheme(theme)
        }
    }
    
    fun setLanguage(lang: String) = _uiState.update { it.copy(language = lang) }

    fun onAdminClick() = _uiState.update { it.copy(showPinDialog = true, pinInput = "", pinError = false) }
    fun onDismissPinDialog() = _uiState.update { it.copy(showPinDialog = false) }
    fun onPinChange(pin: String) = _uiState.update { it.copy(pinInput = pin, pinError = false) }

    fun verifyPin(onSuccess: () -> Unit) {
        if (_uiState.value.pinInput == ADMIN_PIN) {
            _uiState.update { it.copy(showPinDialog = false) }
            onSuccess()
        } else {
            _uiState.update { it.copy(pinError = true) }
        }
    }
}
