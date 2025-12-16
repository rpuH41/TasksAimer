package com.liulkovich.tasksaimer.presentation.screen.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liulkovich.tasksaimer.domain.entity.Notification
import com.liulkovich.tasksaimer.domain.usecase.auth.GetCurrentUserUseCase
import com.liulkovich.tasksaimer.domain.usecase.notification.ClearAllNotificationsUseCase
import com.liulkovich.tasksaimer.domain.usecase.notification.GetNotificationsUseCase
import com.liulkovich.tasksaimer.domain.usecase.notification.GetUnreadNotificationsCountUseCase
import com.liulkovich.tasksaimer.domain.usecase.notification.MarkNotificationAsReadUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getNotificationsUseCase: GetNotificationsUseCase,
    private val getUnreadNotificationsCountUseCase: GetUnreadNotificationsCountUseCase,
    private val markNotificationAsReadUseCase: MarkNotificationAsReadUseCase,
    private val clearAllNotificationsUseCase: ClearAllNotificationsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(NotificationsState())
    val state: StateFlow<NotificationsState> = _state.asStateFlow()

    init {
        observeCurrentUserAndNotifications()
    }

    private fun observeCurrentUserAndNotifications() {
        viewModelScope.launch {
            getCurrentUserUseCase().collectLatest { userId ->
                if (userId == null) {
                    _state.value = NotificationsState(
                        notifications = emptyList(),
                        unreadCount = 0,
                        isLoading = false
                    )
                    return@collectLatest
                }

                getNotificationsUseCase(userId).collectLatest { notifications ->
                    _state.value = NotificationsState(
                        notifications = notifications,
                        unreadCount = notifications.count { !it.isRead },
                        isLoading = false
                    )
                }
//                combine(
//                    getNotificationsUseCase(userId),
//                    getUnreadNotificationsCountUseCase(userId)
//                ) { notifications, unreadCount ->
//                    NotificationsState(
//                        notifications = notifications,
//                        unreadCount = unreadCount,
//                        isLoading = false
//                    )
//                }.collectLatest { newState ->
//                    _state.value = newState
//                }
            }
        }
    }

    fun processCommand(command: NotificationsCommand) {
        when (command) {
            is NotificationsCommand.MarkAsRead -> {
                viewModelScope.launch {
                    val userId = getCurrentUserUseCase().first() ?: return@launch
                    markNotificationAsReadUseCase(userId, command.notificationId)
                }
            }

            NotificationsCommand.ClearAll -> {
                viewModelScope.launch {
                    val userId = getCurrentUserUseCase().first() ?: return@launch
                    clearAllNotificationsUseCase(userId)
                }
            }
        }
    }

}

data class NotificationsState(
    val notifications: List<Notification> = emptyList(),
    val unreadCount: Int = 0,
    val isLoading: Boolean = true
)

sealed interface NotificationsCommand {
    data class MarkAsRead(val notificationId: String) : NotificationsCommand
    data object ClearAll : NotificationsCommand
}