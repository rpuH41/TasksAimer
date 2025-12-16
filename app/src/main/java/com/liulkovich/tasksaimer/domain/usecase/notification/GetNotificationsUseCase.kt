package com.liulkovich.tasksaimer.domain.usecase.notification

import com.liulkovich.tasksaimer.domain.entity.Notification
import com.liulkovich.tasksaimer.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNotificationsUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    operator fun invoke(userId: String): Flow<List<Notification>> {
        return notificationRepository.getNotificationsForUser(userId)
    }
}