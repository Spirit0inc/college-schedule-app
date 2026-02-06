package com.example.collegeschedule.data.dto

data class ScheduleDto(
    val lessonDate: String,
    val weekday: String,
    val lessons: List<LessonDto>
)

data class LessonDto(
    val lessonNumber: Int,
    val time: String,
    val groupParts: Map<String, LessonPartDto>
)

data class LessonPartDto(
    val subject: String,
    val teacher: String,
    val teacherPosition: String,
    val classroom: String,
    val building: String,
    val address: String
)