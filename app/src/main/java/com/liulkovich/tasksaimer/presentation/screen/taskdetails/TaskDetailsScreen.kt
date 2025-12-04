package com.liulkovich.tasksaimer.presentation.screen.taskdetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.liulkovich.tasksaimer.domain.entiity.Status

@Composable
fun TaskDetailsScreen(
    taskId: String,
    taskTitle: String,
    onBack: () -> Unit,
    viewModel: TaskDetailsViewModel = hiltViewModel(),
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
            //.background(Color(0xFF0F172A))
            .padding(16.dp)
    ) {

        Text(
            text = task.title,
            style = MaterialTheme.typography.headlineSmall.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        )

        Box(
            modifier = Modifier
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(16.dp),
                    clip = false
                )
                .clip(RoundedCornerShape(16.dp))
                //.border(width = 1.dp, color = MaterialTheme.colorScheme.outline, shape = RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface,RoundedCornerShape(16.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = "Due: ${task.dueDate}",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(Modifier.height(12.dp))

        //Spacer(Modifier.height(8.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(16.dp),
                    clip = false
                )
                .clip(RoundedCornerShape(16.dp))
                //.border(width = 1.dp, color = MaterialTheme.colorScheme.outline, shape = RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Text(
                text= "Description",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold,
               // modifier = Modifier.padding(horizontal = 12.dp)
            )
            Spacer(Modifier.height(8.dp))

            Text(
                text = task.description.toString(),
                //fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
            )
        }

        Spacer(Modifier.height(12.dp))

        InfoItem(
            title = "Status",
            value = when (task.status) {
                Status.TODO -> "To Do"
                Status.IN_PROGRESS -> "In Progress"
                Status.DONE -> "Done"
            },
            indicatorColor = when (task.status) {
                Status.IN_PROGRESS -> MaterialTheme.colorScheme.tertiary
                Status.DONE -> MaterialTheme.colorScheme.secondary
                Status.TODO -> MaterialTheme.colorScheme.error
            }
        )

        Spacer(Modifier.height(12.dp))

        PersonItem(
            title = "Assignee",
            name = state.assignee?.firstName ?: "-",
            email = state.assignee?.email ?: "-"
        )

        Spacer(Modifier.height(12.dp))

        PersonItem(
            title = "Creator",
            name = state.creator?.firstName ?: "-",
            email = state.creator?.email ?: "-"
        )

        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ActionButton(
                text = "Completed",
                bg = MaterialTheme.colorScheme.secondary,
                onClick = onCompleted
            )
            ActionButton(
                text = "Rework",
                bg = MaterialTheme.colorScheme.tertiary,
                onClick = onRework
            )
            ActionButton(
                text = "Decline",
                bg = MaterialTheme.colorScheme.error,
                onClick = onDecline
            )
        }
    }
}

@Composable
private fun InfoItem(title: String, value: String, indicatorColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false
            )
            .clip(RoundedCornerShape(16.dp))
            //.border(width = 1.dp, color = MaterialTheme.colorScheme.outline, shape = RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(indicatorColor)
                )
                Spacer(Modifier.width(8.dp))
                Text(value, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

@Composable
private fun PersonItem(
    title: String,
    name: String?,
    email: String?
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false
            )
            .clip(RoundedCornerShape(16.dp))
            //.border(width = 1.dp, color = MaterialTheme.colorScheme.outline, shape = RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
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
                       // tint = Color(0xFFCBD5E1)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Text(name ?: "-", color = MaterialTheme.colorScheme.onSurface)


            }

            Text(
                text = email ?: "-",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(horizontal = 48.dp)

            )
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
        Text(text, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
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
