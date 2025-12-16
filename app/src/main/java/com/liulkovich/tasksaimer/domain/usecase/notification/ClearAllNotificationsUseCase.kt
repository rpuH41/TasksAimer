package com.liulkovich.tasksaimer.domain.usecase.notification

import com.liulkovich.tasksaimer.domain.repository.NotificationRepository
import javax.inject.Inject

class ClearAllNotificationsUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(userId: String) {
        notificationRepository.clearAllNotifications(userId)
    }
}