package com.liulkovich.tasksaimer.presentation.screen.createtask

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.liulkovich.tasksaimer.domain.entiity.Priority
import com.liulkovich.tasksaimer.domain.entiity.User
import com.liulkovich.tasksaimer.presentation.components.TaskDueDateTextField
import com.liulkovich.tasksaimer.presentation.components.TaskDueTimeTextField
import com.liulkovich.tasksaimer.presentation.screen.profile.SelectUserPopup

@Composable
fun CreateTaskScreen(
    boardId: String,
    navController: NavHostController,
    viewModel: CreateTaskViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    var showAddContactDialog by remember { mutableStateOf(false) }

    val myContacts by viewModel.myContacts.collectAsState(emptyList())

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
                    item {
                        Spacer(Modifier.height(8.dp))
                    }
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
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "Time",
                                fontSize = 16.sp,
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
                            fontSize = 16.sp,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium,
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Priority.entries.forEach { priority ->
                                FilterChip(
                                    onClick = {
                                        viewModel.processCommand(CreateTaskCommand.SetPriority(priority))
                                    },
                                    label = { Text(priority.name.lowercase().replaceFirstChar { it.uppercase() }) },
                                    selected = creationState.priority == priority,
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = MaterialTheme.colorScheme.surface,
                                        selectedContainerColor = MaterialTheme.colorScheme.primary
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(enabled = false, selected = true),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(46.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(MaterialTheme.colorScheme.surface)
                                        .shadow(
                                            elevation = 4.dp,
                                            shape = RoundedCornerShape(16.dp),
                                            clip = false
                                        )
                                )
                            }
                        }
                    }

                    item {
                        AssignToCard(
                            assignedUsers = myContacts.filter { it.id in creationState.assignedTo },
                            allMyContactUsers = myContacts,
                            onAddUser = { user ->
                                val newList = creationState.assignedTo + user.id
                                viewModel.processCommand(CreateTaskCommand.AssignMembers(newList))
                            },
                            onRemoveUser = { user ->
                                val newList = creationState.assignedTo - user.id
                                viewModel.processCommand(CreateTaskCommand.AssignMembers(newList))
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    //item { Spacer(Modifier.height(80.dp)) }

                    item {
                        Spacer(Modifier.height(20.dp))

                        Button(
                            onClick = {
                                viewModel.processCommand(CreateTaskCommand.SaveTask)
                                navController.popBackStack()
                                      },
                            enabled = creationState.isSaveEnabled,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Create Task", fontWeight = FontWeight.Medium)
                        }
                    }
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
    assignedUsers: List<User>,
    allMyContactUsers: List<User>,
    onAddUser: (User) -> Unit,
    onRemoveUser: (User) -> Unit,
    modifier: Modifier = Modifier
) {

    var showPopup by remember { mutableStateOf(false) }
    val cardColor = MaterialTheme.colorScheme.surface
    val contentColor = MaterialTheme.colorScheme.onSurfaceVariant

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Assigned to",
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 18.sp
                )

                IconButton(onClick = { showPopup = true }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add member")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (assignedUsers.isEmpty()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                            .padding(8.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "No one yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = contentColor
                    )
                }
            } else {
                Column {
                    assignedUsers.forEach { user ->
                        AssignedUserRow(
                            user = user,
                            contentColor = contentColor,
                            onRemoveClick = { onRemoveUser(user) }
                        )
                    }
                }
            }
        }
    }

    if (showPopup) {
        SelectUserPopup(
            users = allMyContactUsers.filter { candidate -> candidate !in assignedUsers },
            onAddClick = {
                onAddUser(it)
                showPopup = false
            },
            onDismiss = { showPopup = false }
        )
    }
}

@Composable
fun AssignedUserRow(
    user: User,
    contentColor: Color,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Avatar",
            tint = contentColor,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = user.firstName ?: "No name",
                style = MaterialTheme.typography.bodyLarge,
                color = contentColor
            )
            Text(
                text = user.email ,
                style = MaterialTheme.typography.bodySmall,
                color = contentColor.copy(alpha = 0.7f)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        IconButton(onClick = onRemoveClick) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun SelectUserPopup(
    users: List<User>,
    onAddClick: (User) -> Unit,
    onDismiss: () -> Unit
) {
    var search by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {

            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                placeholder = { Text("Search") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            val filtered = users.filter { user ->
                user.firstName?.contains(search, ignoreCase = true) == true ||
                        user.email?.contains(search, ignoreCase = true) == true
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                filtered.forEach { user ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(user.firstName ?: "No name")
                            Text(
                                user.email,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 13.sp
                            )
                        }

                        IconButton(onClick = { onAddClick(user) }) {
                            Icon(Icons.Default.Add, contentDescription = "Add")
                        }
                    }
                }
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
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false
            ),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = MaterialTheme.colorScheme.surface,
            errorContainerColor = MaterialTheme.colorScheme.surface
        ),
        value = taskTitle,
        onValueChange = onTaskTitleChange,
        placeholder = {
            Text(
                text = "Title task",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },


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

    TextField(
        value = taskDescription,
        onValueChange = onTaskDescriptionChange,
        placeholder = {
            Text(
                text = "Add a description...",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 160.dp, max = 160.dp)
            .clip(RoundedCornerShape(16.dp))
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false
            ),
        //shape = RoundedCornerShape(12.dp),
        maxLines = 5,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = MaterialTheme.colorScheme.surface,
            errorContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}
