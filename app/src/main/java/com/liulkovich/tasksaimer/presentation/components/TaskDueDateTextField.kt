@file:OptIn(ExperimentalMaterial3Api::class)

package com.liulkovich.tasksaimer.presentation.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.liulkovich.tasksaimer.domain.interactor.DateInputInteractor

@Composable
fun TaskDueDateTextField(
    taskDueDate: String,
    onTaskDueDateChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    dateInputInteractor: DateInputInteractor = remember { DateInputInteractor() }
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var localText by remember { mutableStateOf(taskDueDate) }

    // Синхронизация с внешним состоянием
    LaunchedEffect(taskDueDate) {
        localText = taskDueDate
    }

    OutlinedTextField(
        value = localText,
        onValueChange = { newValue ->
            val formatted = dateInputInteractor.formatUserInput(newValue)
            localText = formatted
            onTaskDueDateChange(formatted)
        },
        label = { Text("mm/dd/yyyy") },
        placeholder = { Text("mm/dd/yyyy") },
        singleLine = true,
        trailingIcon = {
            IconButton(onClick = { showDatePicker = true }) {
                Icon(Icons.Default.DateRange, "Select date")
            }
        },

        //contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = modifier,
    )

    if (showDatePicker) {
        DatePickerModal(
            onDateSelected = { millis ->
                millis?.let {
                    val formatted = dateInputInteractor.formatFromPicker(it)
                    localText = formatted
                    onTaskDueDateChange(formatted)
                }
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

