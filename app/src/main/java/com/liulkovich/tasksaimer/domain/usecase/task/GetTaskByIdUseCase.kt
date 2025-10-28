package com.liulkovich.tasksaimer.domain.usecase.task

import com.liulkovich.tasksaimer.domain.entiity.Task
import com.liulkovich.tasksaimer.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTaskByIdUseCase @Inject constructor(
    private val taskRepository: TaskRepository
)  {
     operator fun invoke(taskId: String): Flow<Task?> = taskRepository.getTaskForId(taskId)

}