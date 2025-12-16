package com.liulkovich.tasksaimer.domain.usecase.task

import com.liulkovich.tasksaimer.domain.entity.Task
import com.liulkovich.tasksaimer.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FilterTasksByStatusUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    operator fun invoke(boardId: String, status: String): Flow<List<Task>>{
        return taskRepository.sortedStatus(boardId, status)
    }
}