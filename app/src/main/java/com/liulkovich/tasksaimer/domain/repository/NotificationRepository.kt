package com.liulkovich.tasksaimer.domain.repository

import com.liulkovich.tasksaimer.domain.entity.Notification
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {

    fun getNotificationsForUser(userId: String): Flow<List<Notification>>

    suspend fun markAsRead(userId: String, notificationId: String)

    suspend fun clearAllNotifications(userId: String)

    suspend fun createNotification(userId: String, notification: Notification)
}