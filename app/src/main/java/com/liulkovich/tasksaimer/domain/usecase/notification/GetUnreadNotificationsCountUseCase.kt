package com.liulkovich.tasksaimer.domain.usecase.notification

import com.liulkovich.tasksaimer.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetUnreadNotificationsCountUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    operator fun invoke(userId: String): Flow<Int> {
        return notificationRepository.getNotificationsForUser(userId).map { list ->
            list.count { !it.isRead }
        }
    }
}