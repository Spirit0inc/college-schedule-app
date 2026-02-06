package com.example.collegeschedule.ui.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
            // Цветной кружок для курса
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
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(schedule) { day ->
            DayCard(day = day)
        }
    }
}

@Composable
fun DayCard(day: ScheduleDto) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Заголовок дня
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = day.weekday,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = day.lessonDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Разделитель
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant,
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Список пар
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                day.lessons.forEach { lesson ->
                    LessonItem(lesson = lesson)
                }
            }

            // Если пар нет
            if (day.lessons.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Нет занятий",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun LessonItem(lesson: com.example.collegeschedule.data.dto.LessonDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Время и номер пары
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

            // Занятия для подгрупп
            lesson.groupParts.forEach { (part, details) ->
                // Цветная полоска для предмета
                val subjectColor = subjectColors[details?.subject] ?: MaterialTheme.colorScheme.primary

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    // Цветная полоска
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(40.dp)
                            .clip(MaterialTheme.shapes.small)
                            .background(subjectColor)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        // Заголовок с предметом
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
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = if (part == "FULL") "Вся группа" else "Подгруппа $part",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Преподаватель
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

                        // Аудитория
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

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 0.5.dp
                )
            }
        }
    }
}

// Главный экран
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

    // Функция загрузки расписания (ДОБАВЛЕНА ЗДЕСЬ)
    fun loadSchedule(group: StudentGroupDto) {
        coroutineScope.launch {
            try {
                val (start, end) = getCurrentWeekDates()
                schedule = RetrofitClient.api.getSchedule(
                    groupName = group.groupName,
                    start = start,
                    end = end
                )
                error = null
            } catch (e: Exception) {
                error = "Ошибка загрузки расписания: ${e.message}"
                schedule = emptyList()
            }
        }
    }

    // При изменении selectedGroupName извне - обновляем выбор
    LaunchedEffect(selectedGroupName) {
        if (selectedGroupName != null && allGroups.isNotEmpty()) {
            val group = allGroups.find { it.groupName == selectedGroupName }
            if (group != null && group != selectedGroup) {
                selectedGroup = group
                searchText = group.groupName
                loadSchedule(group)
            }
        }
    }

    // Загружаем список групп при запуске
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                allGroups = RetrofitClient.api.getAllGroups()
                // Выбираем первую группу по умолчанию
                if (allGroups.isNotEmpty()) {
                    selectedGroup = allGroups[0]
                    loadSchedule(selectedGroup!!)
                }
            } catch (e: Exception) {
                error = "Ошибка загрузки групп: ${e.message}"
            } finally {
                loading = false
            }
        }
    }

    // Фильтрация групп по поиску
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
            // Поиск и выбор группы
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 3.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Заголовок
                    Text(
                        text = "Выбор группы",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Поле поиска
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

                    // Выпадающий список
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
                                            onGroupSelected(group.groupName)
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Выбранная группа
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

            // Расписание
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