package com.liulkovich.tasksaimer.domain.usecase.task

import com.liulkovich.tasksaimer.domain.entity.Task
import com.liulkovich.tasksaimer.domain.repository.TaskRepository
import javax.inject.Inject

class EditTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(task: Task){
        taskRepository.editTask(task)
    }
}