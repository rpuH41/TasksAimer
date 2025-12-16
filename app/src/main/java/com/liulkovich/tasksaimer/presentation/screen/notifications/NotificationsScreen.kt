package com.liulkovich.tasksaimer.presentation.screen.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.liulkovich.tasksaimer.domain.entity.Notification
import com.liulkovich.tasksaimer.domain.entity.NotificationType
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NotificationsScreen(
    viewModel: NotificationsViewModel = hiltViewModel(),
    onNotificationClick: (taskId: String?, boardId: String?, boardTitle: String?) -> Unit,
) {
    val state by viewModel.state.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {

        Box(modifier = Modifier.weight(1f)) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.notifications.isEmpty()) {
                EmptyNotificationsState()
            } else {
                LazyColumn {
                    items(
                        items = state.notifications,
                        key = { it.id }
                    ) { notification ->
                        NotificationItem(
                            notification = notification,
                            onClick = {
                                viewModel.processCommand(
                                    NotificationsCommand.MarkAsRead(notification.id)
                                )
                                onNotificationClick(
                                    notification.taskId,
                                    notification.boardId,
                                    notification.boardTitle
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationItem(
    notification: Notification,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(getIconBackgroundColor(notification.type)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = getIconForType(notification.type),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = notification.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = notification.message,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = formatTimestamp(notification.timestamp),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (!notification.isRead) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                )
            }
        }
    }

    HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
}

@Composable
fun EmptyNotificationsState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.NotificationsOff,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "You're all caught up!",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Check back later for new notifications.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun getIconBackgroundColor(type: NotificationType): androidx.compose.ui.graphics.Color {
    return when (type) {
        NotificationType.TASK_ASSIGNED -> MaterialTheme.colorScheme.primary
        NotificationType.STATUS_CHANGED -> androidx.compose.ui.graphics.Color(0xFF4CAF50) // Зелёный
        NotificationType.DEADLINE_CHANGED -> androidx.compose.ui.graphics.Color(0xFFFF9800) // Оранжевый
        NotificationType.MENTIONED -> androidx.compose.ui.graphics.Color(0xFF9C27B0)     // Фиолетовый
        NotificationType.NEW_COMMENT -> androidx.compose.ui.graphics.Color(0xFF2196F3) // Голубой
    }
}

private fun getIconForType(type: NotificationType): ImageVector {
    return when (type) {
        NotificationType.TASK_ASSIGNED -> Icons.Default.AddTask
        NotificationType.STATUS_CHANGED -> Icons.Default.CheckCircle
        NotificationType.DEADLINE_CHANGED -> Icons.Default.CalendarToday
        NotificationType.MENTIONED -> Icons.Default.AlternateEmail
        NotificationType.NEW_COMMENT -> Icons.Default.Comment
    }
}

private fun formatTimestamp(timestamp: Timestamp?): String {
    if (timestamp == null) return "Just now"

    val date = timestamp.toDate()
    val now = Date()
    val diffInMillis = now.time - date.time

    return when {
        diffInMillis < 60_000 -> "Just now"
        diffInMillis < 3_600_000 -> "${diffInMillis / 60_000}m ago"
        diffInMillis < 86_400_000 -> "${diffInMillis / 3_600_000}h ago"
        diffInMillis < 86_400_000 * 2 -> "Yesterday"
        else -> SimpleDateFormat("dd MMM", Locale.getDefault()).format(date)
    }
}