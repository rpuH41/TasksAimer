@file:OptIn(ExperimentalMaterial3Api::class)

package com.liulkovich.tasksaimer.presentation.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.util.Calendar

@Composable
fun TaskDueTimeTextField(
    taskDueTime: String,
    onTaskDueTimeChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showTimePicker by remember { mutableStateOf(false) }
    var localTime by remember(taskDueTime) { mutableStateOf(taskDueTime) }

    // Синхронизация с внешним состоянием
    LaunchedEffect(taskDueTime) {
        localTime = taskDueTime
    }

    OutlinedTextField(
        value = localTime,
        onValueChange = { input ->
            // Оставляем только цифры
            val digits = input.filter { it.isDigit() }.take(4)
            val formatted = when (digits.length) {
                0 -> ""
                1 -> digits
                2 -> "${digits}:"
                3 -> "${digits.take(2)}:${digits.drop(2)}"
                else -> "${digits.take(2)}:${digits.drop(2)}"
            }
            localTime = formatted
            onTaskDueTimeChange(formatted)
        },
        label = { Text("hh:mm") },
        placeholder = { Text("13:45") },
        singleLine = true,
        trailingIcon = {
            IconButton(onClick = { showTimePicker = true }) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = "Select time"
                )
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    )

    if (showTimePicker) {
        TimePickerModal(
            initialTime = localTime,
            onTimeSelected = { hour, minute ->
                val formatted = String.format("%02d:%02d", hour, minute)
                localTime = formatted
                onTaskDueTimeChange(formatted)
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false }
        )
    }
}

@Composable
fun TimePickerModal(
    initialTime: String = "",
    onTimeSelected: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    val calendar = Calendar.getInstance()
    val (initialHour, initialMinute) = if (initialTime.matches(Regex("\\d{2}:\\d{2}"))) {
        initialTime.split(":").let { it[0].toInt() to it[1].toInt() }
    } else {
        calendar.get(Calendar.HOUR_OF_DAY) to calendar.get(Calendar.MINUTE)
    }

    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )

    TimePickerDialog(
        onDismiss = onDismiss,
        onConfirm = {
            onTimeSelected(timePickerState.hour, timePickerState.minute)
        }
    ) {
        TimePicker(state = timePickerState)
    }
}

@Composable
fun TimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("OK") }
        },
        text = { content() }
    )
}