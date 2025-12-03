package com.liulkovich.tasksaimer.presentation.screen.createtask

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.liulkovich.tasksaimer.domain.entiity.Priority
import com.liulkovich.tasksaimer.presentation.components.TaskDueDateTextField
import com.liulkovich.tasksaimer.presentation.components.TaskDueTimeTextField

@Composable
fun CreateTaskScreen(
    boardId: String,
    navController: NavHostController,
    viewModel: CreateTaskViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()

    val onSaveClick: () -> Unit = {
        viewModel.processCommand(CreateTaskCommand.SaveTask)
        navController.popBackStack()
    }
        when (state) {
            is CreateTaskState.Creation -> {
                val creationState = state as CreateTaskState.Creation

                LazyColumn(
                    modifier = modifier
                        .fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    item { Spacer(Modifier.height(8.dp)) }
                    // Title
                    item {
                        TaskTitleTextField(
                            taskTitle = creationState.title,
                            onTaskTitleChange = {
                                viewModel.processCommand(CreateTaskCommand.TitleTask(it))
                            }
                        )
                    }
                    item {
                        TaskDescriptionTextField(
                            taskDescription = creationState.description ?: "",
                            onTaskDescriptionChange = {
                                viewModel.processCommand(CreateTaskCommand.DescriptionTask(it))
                            }
                        )
                    }
                    item {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Due Date",
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "Time",
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            TaskDueDateTextField(
                                taskDueDate = creationState.dueDate ?: "",
                                onTaskDueDateChange = {
                                    viewModel.processCommand(CreateTaskCommand.DueDate(it))
                                },
                                modifier = Modifier.weight(1f).heightIn(min = 56.dp)
                            )
                            TaskDueTimeTextField(
                                taskDueTime = creationState.dueTime ?: "",
                                onTaskDueTimeChange = {
                                    viewModel.processCommand(CreateTaskCommand.DueTime(it))
                                },
                                modifier = Modifier.weight(1f).heightIn(min = 56.dp)
                            )
                        }
                    }
                    item {
                        Text(
                            text = "Priority",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Priority.entries.forEach { priority ->
                                FilterChip(
                                    onClick = {
                                        viewModel.processCommand(CreateTaskCommand.SetPriority(priority))
                                    },
                                    label = { Text(priority.name.lowercase().replaceFirstChar { it.uppercase() }) },
                                    selected = creationState.priority == priority,
                                    modifier = Modifier.weight(1f).height(46.dp)
                                )
                            }
                        }
                    }
                    // Assign to
                    item {
                        Text(
                            text = "Assign to",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        AssignToCard(
                            assignedMemberIds = creationState.assignedTo,
                            onAddMemberClick = { /* */ }
                        )
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
            else -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }

}

@Composable
fun AssignToCard(
    assignedMemberIds: List<String>,
    onAddMemberClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth().height(72.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxHeight().padding(horizontal = 16.dp)
        ) {
            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.size(40.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    if (assignedMemberIds.isEmpty()) {
                        Icon(Icons.Default.Person, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        Icon(Icons.Default.Group, null, tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (assignedMemberIds.isEmpty()) "No one yet"
                    else "${assignedMemberIds.size} member${if (assignedMemberIds.size > 1) "s" else ""}",
                    style = if (assignedMemberIds.isEmpty()) MaterialTheme.typography.bodyMedium
                    else MaterialTheme.typography.bodyLarge,
                    fontWeight = if (assignedMemberIds.isNotEmpty()) FontWeight.Medium else FontWeight.Normal,
                    color = if (assignedMemberIds.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onSurface
                )
            }

            IconButton(onClick = onAddMemberClick) {
                Icon(Icons.Default.Add, "Add", tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun TaskTitleTextField(
    taskTitle: String,
    onTaskTitleChange: (String) -> Unit
) {

    Text(
        text = "Title",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.Medium
    )
    OutlinedTextField(
        value = taskTitle,
        onValueChange = onTaskTitleChange,
        label = { Text("e.g., Design new logo...") },
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun TaskDescriptionTextField(
    taskDescription: String,
    onTaskDescriptionChange: (String) -> Unit
) {
    Text(
        text = "Description (Optional)",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.Medium
    )

    OutlinedTextField(
        value = taskDescription,
        onValueChange = onTaskDescriptionChange,
        label = { Text("Add a description...") },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 160.dp, max = 160.dp),
        shape = RoundedCornerShape(12.dp),
        maxLines = 5
    )
}
