package com.example.collegeschedule.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.collegeschedule.data.repository.FavoritesRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ScheduleViewModel(application: Application) : AndroidViewModel(application) {
    // Исправлено: передаём application.context
    private val favoritesRepository = FavoritesRepository(application.applicationContext)

    // Избранные группы (Flow)
    val favoriteGroups: StateFlow<Set<String>> = favoritesRepository.favoriteGroups
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    // Добавить группу в избранное
    fun addToFavorites(groupName: String) {
        viewModelScope.launch {
            favoritesRepository.addToFavorites(groupName)
        }
    }

    // Удалить группу из избранного
    fun removeFromFavorites(groupName: String) {
        viewModelScope.launch {
            favoritesRepository.removeFromFavorites(groupName)
        }
    }
}