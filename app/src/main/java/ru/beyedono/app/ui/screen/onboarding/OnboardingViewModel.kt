// file: app/src/main/java/ru/beyedono/app/ui/screen/onboarding/OnboardingViewModel.kt
package ru.beyedono.app.ui.screen.onboarding

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun nextPage() {
        _uiState.update {
            if (it.currentPage < it.totalPages - 1) it.copy(currentPage = it.currentPage + 1) else it
        }
    }

    fun previousPage() {
        _uiState.update {
            if (it.currentPage > 0) it.copy(currentPage = it.currentPage - 1) else it
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[booleanPreferencesKey("is_onboarded")] = true
            }
        }
    }
}