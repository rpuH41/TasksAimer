package com.liulkovich.tasksaimer.domain.usecase.task

import com.liulkovich.tasksaimer.domain.repository.TaskRepository
import javax.inject.Inject

class GetTasksForBoardUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    operator fun invoke(boardId: String) = taskRepository.getTasksForBoard(boardId)
}