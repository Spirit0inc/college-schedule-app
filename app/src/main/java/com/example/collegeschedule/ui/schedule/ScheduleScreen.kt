package com.example.collegeschedule.ui.schedule

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.collegeschedule.data.dto.ScheduleDto
import com.example.collegeschedule.data.dto.StudentGroupDto
import com.example.collegeschedule.data.network.RetrofitClient
import com.example.collegeschedule.utils.getCurrentWeekDates
import kotlinx.coroutines.launch

// Цвета для разных типов занятий
val subjectColors = mapOf(
    "Английский язык" to Color(0xFF2196F3),
    "Математика" to Color(0xFF4CAF50),
    "Программирование" to Color(0xFF9C27B0),
    "Базы данных" to Color(0xFFFF9800),
    "Физика" to Color(0xFFF44336),
    "Химия" to Color(0xFF009688),
    "Биология" to Color(0xFF795548),
    "Информатика" to Color(0xFF607D8B),
    "Физическая культура" to Color(0xFFE91E63)
)

// Цвета для рамок дней недели (сине-фиолетовая гамма)
val dayBorderColors = mapOf(
    "Понедельник" to Color(0xFF6A1B9A),  // Фиолетовый
    "Вторник" to Color(0xFF7E57C2),      // Светло-фиолетовый
    "Среда" to Color(0xFF5C6BC0),        // Индиго
    "Четверг" to Color(0xFF42A5F5),      // Синий
    "Пятница" to Color(0xFF29B6F6),      // Голубой
    "Суббота" to Color(0xFF26C6DA),      // Бирюзовый
    "Воскресенье" to Color(0xFFAB47BC)   // Орхидея
)

// Вспомогательные компоненты
@Composable
fun GroupItem(group: StudentGroupDto, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = group.course.toString(),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }

            Column {
                Text(
                    text = group.groupName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = group.specialtyName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ScheduleList(schedule: List<ScheduleDto>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp, horizontal = 8.dp)
    ) {
        items(schedule) { day ->
            DayCard(day = day)
        }
    }
}

@Composable
fun DayCard(day: ScheduleDto) {
    val borderColor = dayBorderColors[day.weekday] ?: MaterialTheme.colorScheme.primary
    val backgroundColor = Color.White

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = borderColor.copy(alpha = 0.3f)
            )
            .border(
                width = 2.5.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Заголовок дня недели с цветным фоном
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(borderColor.copy(alpha = 0.15f))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = day.weekday.uppercase(),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = borderColor
                    )
                    Text(
                        text = day.lessonDate,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = borderColor.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                day.lessons.forEach { lesson ->
                    LessonItem(lesson = lesson)
                }
            }

            if (day.lessons.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(borderColor.copy(alpha = 0.05f))
                        .border(
                            width = 1.dp,
                            color = borderColor.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Нет занятий",
                        style = MaterialTheme.typography.bodyMedium,
                        color = borderColor.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
fun LessonItem(lesson: com.example.collegeschedule.data.dto.LessonDto) {
    val borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = borderColor,
                shape = MaterialTheme.shapes.medium
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Пара ${lesson.lessonNumber}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = lesson.time,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            lesson.groupParts.forEach { (part, details) ->
                val subjectColor = subjectColors[details?.subject] ?: MaterialTheme.colorScheme.primary

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(40.dp)
                            .clip(MaterialTheme.shapes.small)
                            .background(subjectColor)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = details?.subject ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                modifier = Modifier.weight(1f)
                            )

                            Badge(
                                containerColor = subjectColor.copy(alpha = 0.2f),
                                contentColor = subjectColor
                            ) {
                                Text(
                                    text = if (part == "FULL") "Вся группа" else "Подгруппа $part",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Преподаватель",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = details?.teacher ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Аудитория",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${details?.classroom ?: ""}, ${details?.building ?: ""}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                if (part != lesson.groupParts.keys.lastOrNull()) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.outlineVariant,
                        thickness = 0.5.dp
                    )
                }
            }
        }
    }
}

@Composable
fun ScheduleScreen(
    favoriteGroups: Set<String>,
    selectedGroupName: String?,
    onAddToFavorites: (String) -> Unit,
    onRemoveFromFavorites: (String) -> Unit,
    onGroupSelected: (String) -> Unit
) {
    var allGroups by remember { mutableStateOf<List<StudentGroupDto>>(emptyList()) }
    var selectedGroup by remember { mutableStateOf<StudentGroupDto?>(null) }
    var schedule by remember { mutableStateOf<List<ScheduleDto>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var showDropdown by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    // Функция загрузки расписания
    fun loadSchedule(group: StudentGroupDto) {
        coroutineScope.launch {
            try {
                val (start, end) = getCurrentWeekDates()
                Log.d("NETWORK", "🔄 Загружаем расписание для группы ${group.groupName}")
                schedule = RetrofitClient.api.getSchedule(
                    groupName = group.groupName,
                    start = start,
                    end = end
                )
                Log.d("NETWORK", "✅ Расписание загружено: ${schedule.size} дней")
                error = null
            } catch (e: Exception) {
                Log.e("NETWORK", "❌ Ошибка загрузки расписания: ${e.message}", e)
                error = "Ошибка загрузки расписания: ${e.message}"
                schedule = emptyList()
            }
        }
    }

    // Ключевое исправление: сохраняем previousSelectedGroupName для сравнения
    var previousSelectedGroupName by remember { mutableStateOf<String?>(null) }

    // При изменении selectedGroupName извне (например, из избранного)
    LaunchedEffect(selectedGroupName) {
        if (selectedGroupName != null && selectedGroupName != previousSelectedGroupName) {
            Log.d("SCHEDULE_SCREEN", "🎯 Получена новая группа из AppState: $selectedGroupName")
            previousSelectedGroupName = selectedGroupName

            if (allGroups.isNotEmpty()) {
                val group = allGroups.find { it.groupName == selectedGroupName }
                if (group != null) {
                    selectedGroup = group
                    searchText = group.groupName
                    loadSchedule(group)
                }
            }
        }
    }

    // Загружаем список групп при запуске
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                allGroups = RetrofitClient.api.getAllGroups()
                Log.d("NETWORK", "✅ Успешно! Загружено ${allGroups.size} групп")

                if (allGroups.isNotEmpty()) {
                    // Если есть выбранная группа из параметров - используем её
                    // Иначе берем первую группу
                    val groupToSelect = if (selectedGroupName != null) {
                        allGroups.find { it.groupName == selectedGroupName } ?: allGroups[0]
                    } else {
                        allGroups[0]
                    }

                    selectedGroup = groupToSelect
                    searchText = groupToSelect.groupName

                    // Сохраняем в AppState, если это первый запуск
                    if (selectedGroupName == null) {
                        Log.d("SCHEDULE_SCREEN", "🚀 Первый запуск, сохраняем группу: ${groupToSelect.groupName}")
                        onGroupSelected(groupToSelect.groupName)
                    }

                    loadSchedule(groupToSelect)
                } else {
                    Log.w("NETWORK", "⚠️ Нет групп")
                    error = "Нет доступных групп"
                }
            } catch (e: Exception) {
                Log.e("NETWORK", "❌ Ошибка загрузки групп: ${e.message}", e)
                error = "Ошибка загрузки групп: ${e.message}"
            } finally {
                loading = false
            }
        }
    }

    // Фильтрация групп
    val filteredGroups = if (searchText.isEmpty()) {
        allGroups
    } else {
        allGroups.filter { group ->
            group.groupName.contains(searchText, ignoreCase = true) ||
                    group.specialtyName.contains(searchText, ignoreCase = true)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 3.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Выбор группы",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = searchText,
                        onValueChange = {
                            searchText = it
                            showDropdown = true
                        },
                        label = { Text("Введите название группы") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Поиск",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { showDropdown = !showDropdown }) {
                                Icon(
                                    Icons.Default.ArrowDropDown,
                                    contentDescription = "Список",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    if (showDropdown && filteredGroups.isNotEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 200.dp),
                            elevation = CardDefaults.cardElevation(8.dp)
                        ) {
                            LazyColumn {
                                items(filteredGroups) { group ->
                                    GroupItem(
                                        group = group,
                                        onClick = {
                                            selectedGroup = group
                                            showDropdown = false
                                            searchText = group.groupName
                                            loadSchedule(group)
                                            // Сохраняем выбор в AppState
                                            onGroupSelected(group.groupName)
                                        }
                                    )
                                }
                            }
                        }
                    }

                    selectedGroup?.let { group ->
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.surfaceContainerLow,
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = group.groupName,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "${group.course} курс • ${group.specialtyName}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                val isFavorite = group.groupName in favoriteGroups
                                IconButton(
                                    onClick = {
                                        if (isFavorite) {
                                            onRemoveFromFavorites(group.groupName)
                                        } else {
                                            onAddToFavorites(group.groupName)
                                        }
                                    },
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isFavorite) {
                                            Icons.Filled.Star
                                        } else {
                                            Icons.Outlined.Star
                                        },
                                        contentDescription = "Избранное",
                                        tint = if (isFavorite) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                        },
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when {
                loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                strokeWidth = 3.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Загрузка расписания...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                error != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Ошибка",
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Ошибка: $error",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
                schedule.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Нет расписания",
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Нет занятий на эту неделю",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                else -> {
                    ScheduleList(schedule = schedule)
                }
            }
        }
    }
}