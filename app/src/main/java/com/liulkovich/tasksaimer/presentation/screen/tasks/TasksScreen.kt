package com.liulkovich.tasksaimer.presentation.screen.tasks

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.EditCalendar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
    boardId: String,
    boardTitle: String,
    onCreateTaskClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: TasksViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(boardId) {
        viewModel.processCommand(TaskCommand.SetBoardId(boardId))
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(Modifier.height(8.dp)) }

        item {
            TaskFilter()
        }

        item { Spacer(Modifier.height(16.dp)) }

        // Состояния
        if (state.isLoading) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(64.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
        } else if (state.error != null) {
            item {
                Text(
                    text = state.error!!,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(32.dp)
                )
            }
        } else if (state.tasks.isEmpty()) {
            item {
                Text(
                    text = "No tasks yet",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(top = 80.dp)
                )
            }
        } else {
            itemsIndexed(
                items = state.tasks,
                key = { _, task -> task.id ?: task.id ?: task.hashCode() }
            ) { _, task ->
                TaskCard(
                    task = task,
                    onDetailsClick = { /* */ }
                )
            }
        }

        // Отступ снизу под FAB и BottomBar
        item { Spacer(Modifier.height(100.dp)) }
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
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier
            .fillMaxWidth()
            //.width(280.dp)
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
    onBackTaskClick: () -> Unit

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
            IconButton( onClick = { onBackTaskClick() } ) {
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
fun TaskFilter(modifier: Modifier = Modifier) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    val filters = listOf("All", "To Do", "In Progress", "Done")

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filters.forEachIndexed { index, label ->
            val isSelected = index == selectedIndex

            // Используем обычный TextButton + кастомный фон
            TextButton(
                onClick = { selectedIndex = index },
                modifier = Modifier
                    .height(40.dp)
                    .background(
                        color = if (isSelected)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(20.dp)
                    ),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                Text(
                    text = label,
                    color = if (isSelected)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                    fontSize = 14.sp
                )
            }
        }
    }
}