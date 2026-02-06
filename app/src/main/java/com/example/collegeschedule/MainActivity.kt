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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.collegeschedule.ui.favorites.FavoritesScreen
import com.example.collegeschedule.ui.schedule.ScheduleScreen
import com.example.collegeschedule.ui.theme.CollegeScheduleTheme
import com.example.collegeschedule.viewmodel.ScheduleViewModel

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

// Упрощенный AppState
class AppState {
    var selectedGroupName by mutableStateOf<String?>(null)

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
    val viewModel: ScheduleViewModel = viewModel()

    // Состояние избранных групп получаем из ViewModel
    val favoriteGroups by viewModel.favoriteGroups.collectAsState(initial = emptySet())

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
                    favoriteGroups = favoriteGroups,
                    selectedGroupName = appState.selectedGroupName,
                    onAddToFavorites = { groupName ->
                        viewModel.addToFavorites(groupName)
                    },
                    onRemoveFromFavorites = { groupName ->
                        viewModel.removeFromFavorites(groupName)
                    },
                    onGroupSelected = { groupName ->
                        appState.selectGroup(groupName)
                    }
                )
            }
            composable("favorites") {
                FavoritesScreen(
                    favoriteGroups = favoriteGroups,
                    onGroupSelected = { groupName ->
                        // ВАЖНО: сразу сохраняем группу и переходим
                        appState.selectGroup(groupName)
                        navController.navigate("schedule") {
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onRemoveFromFavorites = { groupName ->
                        viewModel.removeFromFavorites(groupName)
                    }
                )
            }
        }
    }
}