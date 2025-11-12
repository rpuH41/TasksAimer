package com.liulkovich.tasksaimer.presentation.screen.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.EditCalendar
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.liulkovich.tasksaimer.domain.entiity.Status
import com.liulkovich.tasksaimer.domain.entiity.Task

@Composable
fun TasksScreen(
    modifier: Modifier = Modifier,
    viewModel: TasksViewModel = hiltViewModel(),
    boardTitle: String
){
    val state by viewModel.state.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {  },
                containerColor = MaterialTheme.colorScheme.onSecondary,
                contentColor = MaterialTheme.colorScheme.surface,
                shape = CircleShape
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add task")
            }
        },
        topBar = {
            TopAppTaskBar(
                boardTitle = boardTitle,
            )
        },
        bottomBar = {
            NavigationTasksAimerBar()
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
            }
            item {
                TaskFilter()
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
            if (state.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
            } else if (state.error != null) {
                item {
                    Text(
                        text = state.error!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else if (state.tasks.isEmpty()) {
                item {
                    Text(
                        text = "No tasks found",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                itemsIndexed(
                    items = state.tasks,
                    key = { index, task -> task.id?: index }  // ← КЛЮЧ!
                ) { index, task ->
                    TaskCard(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        task = task,
                        onDetailsClick = { /* Открыть задачу */ }
                    )


                    /*BoardCard(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        board = board,
                        onNoteClick = { /* Открыть доску */ }
                    )
                    if (index < state.boards.lastIndex) {
                        Spacer(modifier = Modifier.height(12.dp))
                    }*/
                }
            }
        }
    }

}

@Composable
fun TaskCard(
    modifier: Modifier = Modifier,
    task: Task,
    onDetailsClick: () -> Unit

) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        modifier = modifier
            .width(280.dp)
            .height(100.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status Icon
            val (statusIcon, statusColor) = when (task.status) {
                Status.TODO -> Icons.Default.RadioButtonUnchecked to Color(0xFFE0E0E0)
                Status.IN_PROGRESS -> Icons.Default.HourglassBottom to Color(0xFFFFB74D)
                Status.DONE -> Icons.Default.CheckCircle to Color(0xFF4CAF50)
            }

            Icon(
                imageVector = statusIcon,
                contentDescription = null,
                tint = statusColor,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Title + Status + Date/Time
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    val (statusText, textColor) = when (task.status) {
                        Status.TODO -> "To Do" to Color(0xFFBDBDBD)
                        Status.IN_PROGRESS -> "In Progress" to Color(0xFFFFB74D)
                        Status.DONE -> "Done" to Color(0xFF4CAF50)
                    }

                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelMedium,
                        color = textColor,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Icon(
                        imageVector = Icons.Outlined.EditCalendar,
                        contentDescription = "Due date",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = formatDueDateTime(task.dueDate, task.dueTime),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Avatars (assignedTo)
            Row {
                task.assignedTo.take(2).forEachIndexed { index, _ ->
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .offset(x = (-12 * index).dp)
                            .clip(CircleShape)
                            .background(randomColor(index))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(6.dp)
                        )
                    }
                }
            }
        }
    }
}

// Вспомогательные функции (добавь внизу файла)
private fun formatDueDateTime(dueDate: String?, dueTime: String?): String {
    if (dueDate == null && dueTime == null) return "No due date"
    if (dueDate == null) return dueTime ?: ""
    if (dueTime == null) return dueDate
    return "$dueDate, $dueTime"
}

private fun randomColor(seed: Int): Color {
    val colors = listOf(
        Color(0xFF6C5CE7), Color(0xFFA29BFE), Color(0xFF74B9FF),
        Color(0xFF00B894), Color(0xFF55E6C1)
    )
    return colors[seed % colors.size]
}

@Composable
fun TopAppTaskBar(
    modifier: Modifier = Modifier,
    boardTitle: String,
    //onBackTaskClick: () -> Unit

){
    @OptIn(ExperimentalMaterial3Api::class)
    TopAppBar(

        title= {
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = boardTitle,
                fontSize = 22.sp,
                        textAlign = TextAlign.Center
            )
               },
        navigationIcon={
            IconButton( onClick = {  } ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
                       },
        actions={
            IconButton({ }) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Find task"
                )
            }
        },
        colors= TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = Color.LightGray,
            navigationIconContentColor = Color.LightGray,
            actionIconContentColor = Color.LightGray))

}

@Composable
fun TaskFilter(
    modifier: Modifier = Modifier,
    //onFilterChanged: (String) -> Unit
    ) {
        var selectedFilter by remember { mutableIntStateOf(0) }  // 0 = "All"

        val filters = listOf("All", "To Do", "In Progress", "Done")

        SingleChoiceSegmentedButtonRow(  // ← Single-select
            modifier = modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(horizontal = 5.dp, vertical = 4.dp)
        ) {
            filters.forEachIndexed { index, filter ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = filters.size  // ← Скругление углов
                    ),
                    onClick = {
                        selectedFilter = index
                        //onFilterChanged(filter)
                    },
                    selected = index == selectedFilter,
                    label = { Text(filter) }
                )
            }
        }
}

@Composable
fun NavigationTasksAimerBar(modifier: Modifier = Modifier) {

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

