package com.liulkovich.tasksaimer.domain.usecase.notification

import com.liulkovich.tasksaimer.domain.repository.NotificationRepository
import javax.inject.Inject

class MarkNotificationAsReadUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(userId: String, notificationId: String) {
        notificationRepository.markAsRead(userId, notificationId)
    }
}