package com.liulkovich.tasksaimer.presentation.screen.createtask

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.liulkovich.tasksaimer.domain.entiity.Priority
import com.liulkovich.tasksaimer.presentation.components.TaskDueDateTextField
import com.liulkovich.tasksaimer.presentation.components.TaskDueTimeTextField
import com.liulkovich.tasksaimer.presentation.screen.createboard.NavigationCreateTasksAimerBar

@Composable
fun CreateTaskScreen(
    modifier: Modifier = Modifier,
    viewModel: CreateTaskViewModel = hiltViewModel(),
){
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppCreateTaskBar(
                onSaveTaskChange = {
                    viewModel.processCommand(CreateTaskCommand.SaveTask)
                },
                onBackCreateTaskChange = {
                    viewModel.processCommand(CreateTaskCommand.Back)
                }
            )
        },
        bottomBar = {
            NavigationCreateTasksAimerBar()
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding
        ) {
            item {
                Spacer(modifier = modifier.height(20.dp))
            }
            if (state is CreateTaskState.Creation) {
                val creationState = state as CreateTaskState.Creation

                item {
                    TaskTitleTextField(
                        taskTitle = creationState.title,
                        onTaskTitleChange = {
                            viewModel.processCommand(CreateTaskCommand.TitleTask(it))
                        }
                    )
                }

                item{
                    Spacer(modifier = modifier.height(20.dp))
                }

                item {
                    TaskDescriptionTextField(
                        taskDescription = creationState.description ?: "",
                        onTaskDescriptionChange = {
                            viewModel.processCommand(CreateTaskCommand.DescriptionTask(it))
                        }
                    )
                }

                item{
                    Spacer(modifier = modifier.height(20.dp))
                }
                item {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                modifier = Modifier
                                    .padding(start = 10.dp)
                                    .weight(1f),
                                text = "Due Date",
                                style = TextStyle(
                                    fontSize = 19.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                textAlign = TextAlign.Start
                            )
                            Text(
                                modifier = Modifier
                                    .padding(start = 10.dp)
                                    .weight(1f),
                                text = "Time",
                                style = TextStyle(
                                    fontSize = 19.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                textAlign = TextAlign.Start
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            TaskDueDateTextField(
                                taskDueDate = creationState.dueDate ?: "",
                                onTaskDueDateChange = {
                                    viewModel.processCommand(CreateTaskCommand.DueDate(it))
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp)
                                    .padding(start = 10.dp)
                            )

                            TaskDueTimeTextField(
                                taskDueTime = creationState.dueTime ?: "",
                                onTaskDueTimeChange = {
                                    viewModel.processCommand(CreateTaskCommand.DueTime(it))
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp)
                                    .padding(start = 5.dp, end = 10.dp)

                            )
                        }
                }
                item{
                    Spacer(modifier = modifier.height(20.dp))
                }
                item {
                    Text(
                        text = "Priority",
                        style = TextStyle(
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.padding(start = 10.dp, end = 10.dp, bottom = 10.dp )
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Priority.entries.forEach { priority ->
                            FilterChip(
                                onClick = {
                                    viewModel.processCommand(CreateTaskCommand.SetPriority(priority))
                                },
                                label = { Text(priority.name.lowercase().replaceFirstChar { it.uppercase() }) },
                                selected = creationState.priority == priority,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .padding(start = 10.dp, end = 10.dp),
                            )
                        }
                    }
                }
                item {
                    Spacer(modifier = modifier.height(20.dp))
                }
                item {
                    Text(
                        text = "Assign to",
                        style = TextStyle(
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.padding(start = 10.dp, end = 10.dp, bottom = 10.dp )
                    )
                }
                item {
                    AssignToCard(
                        assignedMemberIds = creationState.assignedTo,
                        onAddMemberClick = {  },
                        modifier = Modifier.padding(start = 10.dp, end = 10.dp, bottom = 10.dp )
                    )
                }
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
        modifier = Modifier
            .padding(start = 10.dp, end = 10.dp),
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
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp),
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun TaskDescriptionTextField(
    taskDescription: String,
    onTaskDescriptionChange: (String) -> Unit
) {
    Text(
        modifier = Modifier
            .padding(start = 10.dp, end = 10.dp),
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
            .heightIn(min = 160.dp, max = 160.dp)
            .padding(start = 10.dp, end = 10.dp),
        shape = RoundedCornerShape(12.dp),
        maxLines = 5
    )
}

@Composable
fun TopAppCreateTaskBar(
    modifier: Modifier = Modifier,
    onSaveTaskChange: () -> Unit,
    onBackCreateTaskChange: () -> Unit,

){
    @OptIn(ExperimentalMaterial3Api::class)
    TopAppBar(
        title = {
            Text(
                modifier = modifier
                    .fillMaxWidth(),
                text = "Create Task",
                fontSize = 22.sp,
                textAlign = TextAlign.Center
            )
        },
        navigationIcon = {
            IconButton( onClick = { onBackCreateTaskChange() } ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Back"
                )
            }
        },
        actions = {
            TextButton(onClick = { onSaveTaskChange() }) {
                Text(
                    text = "Save",
                    style = TextStyle(
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.primary,
                        textDecoration = TextDecoration.Underline
                    )
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = Color.LightGray,
            navigationIconContentColor = Color.LightGray,
            actionIconContentColor = Color.LightGray))

}

@Composable
fun NavigationCreateTasksAimerBar(modifier: Modifier = Modifier) {

    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf(
        "Boards",
        "Notifications",
        "Profile"
    )
    val selectedIcons = listOf(
        Icons.Filled.Dashboard,
        Icons.Filled.Notifications,
        Icons.Filled.Person
    )
    val unselectedIcons =
        listOf(
            Icons.Outlined.Dashboard,
            Icons.Outlined.Notifications,
            Icons.Outlined.Person
        )
    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
        ,
        containerColor = MaterialTheme.colorScheme.surface
    ) {

        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        if (selectedItem == index) selectedIcons[index] else unselectedIcons[index],
                        contentDescription = item,
                    )
                },
                label = { Text(item) },
                selected = selectedItem == index,
                onClick = { selectedItem = index },
            )
        }
    }
}

