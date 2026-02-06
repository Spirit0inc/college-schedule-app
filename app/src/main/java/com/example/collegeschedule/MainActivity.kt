package com.example.collegeschedule

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.collegeschedule.ui.favorites.FavoritesScreen
import com.example.collegeschedule.ui.schedule.ScheduleScreen
import com.example.collegeschedule.ui.theme.CollegeScheduleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CollegeScheduleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CollegeScheduleApp()
                }
            }
        }
    }
}

// Ключевое изменение: сохраняем состояние ВНЕ навигации
class AppState {
    var favoriteGroups by mutableStateOf(emptySet<String>())
    var selectedGroupName by mutableStateOf<String?>(null)

    fun addToFavorites(groupName: String) {
        favoriteGroups = favoriteGroups + groupName
    }

    fun removeFromFavorites(groupName: String) {
        favoriteGroups = favoriteGroups - groupName
    }

    fun selectGroup(groupName: String) {
        selectedGroupName = groupName
    }
}

@Composable
fun rememberAppState(): AppState {
    return remember { AppState() }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollegeScheduleApp() {
    val navController = rememberNavController()
    val appState = rememberAppState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Расписание колледжа") }
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                tonalElevation = 0.dp
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Расписание") },
                    label = { Text("Расписание") },
                    selected = true,
                    onClick = {
                        // Просто переключаемся на экран
                        navController.navigate("schedule") {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Star, contentDescription = "Избранное") },
                    label = { Text("Избранное") },
                    selected = false,
                    onClick = {
                        navController.navigate("favorites") {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "schedule",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("schedule") {
                ScheduleScreen(
                    favoriteGroups = appState.favoriteGroups,
                    selectedGroupName = appState.selectedGroupName,
                    onAddToFavorites = { groupName ->
                        appState.addToFavorites(groupName)
                    },
                    onRemoveFromFavorites = { groupName ->
                        appState.removeFromFavorites(groupName)
                    },
                    onGroupSelected = { groupName ->
                        appState.selectGroup(groupName)
                    }
                )
            }
            composable("favorites") {
                FavoritesScreen(
                    favoriteGroups = appState.favoriteGroups,
                    onGroupSelected = { groupName ->
                        // Сохраняем группу и переходим
                        appState.selectGroup(groupName)
                        navController.navigate("schedule") {
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onRemoveFromFavorites = { groupName ->
                        appState.removeFromFavorites(groupName)
                    }
                )
            }
        }
    }
}