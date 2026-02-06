package com.example.collegeschedule.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Создаём DataStore
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "favorites")

class FavoritesRepository(context: Context) {
    private val dataStore = context.dataStore

    // Ключ для хранения избранных групп
    private val FAVORITE_GROUPS_KEY = stringSetPreferencesKey("favorite_groups")

    // Получить все избранные группы
    val favoriteGroups: Flow<Set<String>> = dataStore.data
        .map { preferences ->
            preferences[FAVORITE_GROUPS_KEY] ?: emptySet()
        }

    // Добавить группу в избранное
    suspend fun addToFavorites(groupName: String) {
        dataStore.edit { preferences ->
            val currentFavorites = preferences[FAVORITE_GROUPS_KEY] ?: emptySet()
            preferences[FAVORITE_GROUPS_KEY] = currentFavorites + groupName
        }
    }

    // Удалить группу из избранного
    suspend fun removeFromFavorites(groupName: String) {
        dataStore.edit { preferences ->
            val currentFavorites = preferences[FAVORITE_GROUPS_KEY] ?: emptySet()
            preferences[FAVORITE_GROUPS_KEY] = currentFavorites - groupName
        }
    }
}