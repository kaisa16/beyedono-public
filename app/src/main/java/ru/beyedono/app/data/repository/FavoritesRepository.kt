// file: app/src/main/java/ru/beyedono/app/data/repository/FavoritesRepository.kt
package ru.beyedono.app.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoritesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private val FAVORITES_KEY = stringSetPreferencesKey("favorite_product_ids")

    val favorites: Flow<Set<String>> = dataStore.data.map { prefs ->
        prefs[FAVORITES_KEY] ?: emptySet()
    }

    suspend fun toggleFavorite(productId: String) {
        dataStore.edit { prefs ->
            val current = prefs[FAVORITES_KEY] ?: emptySet()
            prefs[FAVORITES_KEY] = if (current.contains(productId)) current - productId else current + productId
        }
    }

    suspend fun clearFavorites() {
        dataStore.edit { it.remove(FAVORITES_KEY) }
    }
}
