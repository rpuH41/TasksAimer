package com.liulkovich.tasksaimer.domain.entity

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp

data class Notification(
    val id: String = "",
    val type: NotificationType = NotificationType.TASK_ASSIGNED,
    val title: String = "",
    val message: String = "",
    val taskId: String? = null,
    val boardId: String? = null,
    val boardTitle: String? = null,
    val fromUserId: String? = null,
    @ServerTimestamp val timestamp: Timestamp? = null,

    @get:PropertyName("isRead")
    @set:PropertyName("isRead")
    var isRead: Boolean = false,
)
