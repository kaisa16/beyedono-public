// file: app/src/main/java/ru/beyedono/app/ui/screen/splash/SplashViewModel.kt
package ru.beyedono.app.ui.screen.splash

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ru.beyedono.app.data.local.dao.UserDao
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userDao: UserDao,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Loading)
    val uiState: StateFlow<SplashUiState> = _uiState

    init {
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            val user = userDao.getUser().first()
            val isOnboarded = dataStore.data.first()[booleanPreferencesKey("is_onboarded")] ?: false

            if (user != null) {
                _uiState.value = SplashUiState.NavigateToMenu
            } else if (isOnboarded) {
                _uiState.value = SplashUiState.NavigateToMenu // Ideally to login, but menu for guest
            } else {
                _uiState.value = SplashUiState.NavigateToOnboarding
            }
        }
    }
}