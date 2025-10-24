package com.liulkovich.tasksaimer.domain.usecase.task

import com.liulkovich.tasksaimer.domain.entiity.Task
import com.liulkovich.tasksaimer.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchTaskByTitleUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {

    operator fun invoke(boardId: String, title: String): Flow<List<Task>> {
        return taskRepository.searchTaskByTitle(boardId, title)
    }
}