package com.liulkovich.tasksaimer.domain.usecase.task

import com.liulkovich.tasksaimer.domain.repository.TaskRepository
import javax.inject.Inject

class DeleteTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(taskId: String){
        taskRepository.deleteTask(taskId)
    }
}