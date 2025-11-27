package com.liulkovich.tasksaimer.presentation.screen.taskdetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun TaskDetailsScreen(
    viewModel: TaskDetailsViewModel = hiltViewModel(),
    taskId: String,
    taskTitle: String,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is TaskDetailsEffect.NavigateBack -> onBack()
                else -> {}
            }
        }
    }

    when (state) {
        TaskDetailsState.Loading -> LoadingState()
        is TaskDetailsState.Error -> ErrorState((state as TaskDetailsState.Error).message)
        is TaskDetailsState.Success -> TaskDetailsContent(
            state = state as TaskDetailsState.Success,
            onCompleted = { viewModel.processCommand(TaskDetailsCommand.MarkAsCompleted) },
            onRework = { viewModel.processCommand(TaskDetailsCommand.MarkAsRework) },
            onDecline = { viewModel.processCommand(TaskDetailsCommand.DeclineTask) },
        )
    }
}

@Composable
private fun TaskDetailsContent(
    state: TaskDetailsState.Success,
    onCompleted: () -> Unit,
    onRework: () -> Unit,
    onDecline: () -> Unit,
) {
    val task = state.task

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(16.dp)
    ) {

        // ---------- Title ----------
        Text(
            text = task.title,
            style = MaterialTheme.typography.headlineSmall.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(Modifier.height(4.dp))

        // ---------- Due date ----------
        Box(
            modifier = Modifier
                .background(Color(0xFF1E293B), RoundedCornerShape(16.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = "Due: ${task.dueDate}",
                color = Color(0xFF38BDF8),
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(Modifier.height(16.dp))

        // ---------- Description ----------
        Text(
            "Description",
            color = Color.White,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E293B), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Text(
                text = task.description.toString(),
                color = Color(0xFFD0D4DD),
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
            )
        }

        Spacer(Modifier.height(20.dp))

        // ---------- Status ----------
        InfoItem(
            title = "Status",
            value = when (task.status) {
                com.liulkovich.tasksaimer.domain.entiity.Status.TODO -> "To Do"
                com.liulkovich.tasksaimer.domain.entiity.Status.IN_PROGRESS -> "In Progress"
                com.liulkovich.tasksaimer.domain.entiity.Status.DONE -> "Done"
            },
            indicatorColor = when (task.status) {
                com.liulkovich.tasksaimer.domain.entiity.Status.IN_PROGRESS -> Color(0xFF38BDF8)
                com.liulkovich.tasksaimer.domain.entiity.Status.DONE -> Color(0xFF22C55E)
                com.liulkovich.tasksaimer.domain.entiity.Status.TODO -> Color.Gray
            }
        )

        Spacer(Modifier.height(12.dp))

        // ---------- Assignee ----------
        PersonItem(
            title = "Assignee",
            name = task.id
        )

        Spacer(Modifier.height(12.dp))

        // ---------- Creator ----------
        PersonItem(
            title = "Creator",
            name = task.assignedTo.toString()
        )

        Spacer(Modifier.weight(1f))

        // ---------- Bottom Buttons ----------
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ActionButton("Completed", Color(0xFF22C55E), onCompleted)
            ActionButton("Rework", Color(0xFFFACC15), onRework)
            ActionButton("Decline", Color(0xFFEF4444), onDecline)
        }
    }
}

@Composable
private fun InfoItem(title: String, value: String, indicatorColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1E293B), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(title, color = Color(0xFFCBD5E1))
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(indicatorColor)
                )
                Spacer(Modifier.width(8.dp))
                Text(value, color = Color.White)
            }
        }
    }
}

@Composable
private fun PersonItem(title: String, name: String?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1E293B), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(title, color = Color(0xFFCBD5E1))
            Spacer(Modifier.height(6.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF334155)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = Color(0xFFCBD5E1)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Text(name ?: "-", color = Color.White)
            }
        }
    }
}

@Composable
private fun ActionButton(text: String, bg: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = bg),
        shape = RoundedCornerShape(50),
        modifier = Modifier
            //.weight(1f)
            .padding(horizontal = 4.dp)
    ) {
        Text(text, color = Color.Black, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun LoadingState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = Color.White)
    }
}

@Composable
private fun ErrorState(message: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = message, color = Color.Red)
    }
}
