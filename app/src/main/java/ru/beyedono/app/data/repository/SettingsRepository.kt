// file: app/src/main/java/ru/beyedono/app/data/repository/SettingsRepository.kt
package ru.beyedono.app.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private val THEME_KEY = stringPreferencesKey("app_theme")

    val theme: Flow<String> = dataStore.data.map { prefs ->
        prefs[THEME_KEY] ?: "system"
    }

    suspend fun setTheme(theme: String) {
        dataStore.edit { prefs ->
            prefs[THEME_KEY] = theme
        }
    }
}
