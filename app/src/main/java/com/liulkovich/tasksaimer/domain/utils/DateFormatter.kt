package com.liulkovich.tasksaimer.domain.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateFormatter {
    private val fmt = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())

    // миллисекунды → "12/12/2025"
    fun fromMillis(millis: Long): String = fmt.format(Date(millis))

    //  только цифры → "12/34/56"
    fun fromDigits(digits: String): String = buildString {
        digits.forEachIndexed { i, c ->
            append(c)
            if (i == 1 || i == 3) append('/')
        }
    }.take(10)

    // проверка, что строка – правильная дата
    fun isValid(date: String): Boolean =
        try { fmt.parse(date) != null } catch (e: Exception) { false }
}