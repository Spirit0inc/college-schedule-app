package com.example.collegeschedule.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun getCurrentWeekDates(): Pair<String, String> {
    val today = LocalDate.now()
    val formatter = DateTimeFormatter.ISO_DATE

    // Если сегодня воскресенье — начинаем с понедельника
    var start = if (today.dayOfWeek.value == 7) today.plusDays(1) else today

    // Нужно получить 6 учебных дней (ПН-СБ)
    var daysAdded = 0
    var end = start
    while (daysAdded < 5) {
        end = end.plusDays(1)
        if (end.dayOfWeek.value != 7) { // Не воскресенье
            daysAdded++
        }
    }

    return start.format(formatter) to end.format(formatter)
}