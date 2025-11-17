package com.liulkovich.tasksaimer.domain.interactor

import com.liulkovich.tasksaimer.domain.utils.DateFormatter
import javax.inject.Inject

class DateInputInteractor @Inject constructor() {

    fun formatUserInput(rawInput: String): String {
        val digits = rawInput.filter { it.isDigit() }
        return DateFormatter.fromDigits(digits)
    }

    fun formatFromPicker(millis: Long): String {
        return DateFormatter.fromMillis(millis)
    }

    fun isValid(dateString: String): Boolean {
        return DateFormatter.isValid(dateString)
    }
}