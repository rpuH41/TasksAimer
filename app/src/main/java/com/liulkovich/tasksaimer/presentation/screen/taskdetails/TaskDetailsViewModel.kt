package com.liulkovich.tasksaimer.presentation.screen.taskdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liulkovich.tasksaimer.domain.entiity.Status
import com.liulkovich.tasksaimer.domain.entiity.Task
import com.liulkovich.tasksaimer.domain.usecase.task.DeleteTaskUseCase
import com.liulkovich.tasksaimer.domain.usecase.task.EditTaskUseCase
import com.liulkovich.tasksaimer.domain.usecase.task.GetTaskByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskDetailsViewModel @Inject constructor(
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
    private val editTaskUseCase: EditTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val taskId: String = savedStateHandle["taskId"]
        ?: throw IllegalStateException("taskId is required")

    private val _state = MutableStateFlow<TaskDetailsState>(TaskDetailsState.Loading)
    val state = _state.asStateFlow()

    private val _effect = Channel<TaskDetailsEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        getTaskByIdUseCase(taskId)
            .onStart { _state.value = TaskDetailsState.Loading }
            .catch { _state.value = TaskDetailsState.Error(it.message ?: "Failed to load task") }
            .onEach { task ->
                _state.value = task?.let { TaskDetailsState.Success(it) }
                    ?: TaskDetailsState.Error("Task was deleted")
            }
            .launchIn(viewModelScope)
    }

    fun processCommand(command: TaskDetailsCommand) {
        val currentState = _state.value
        if (currentState !is TaskDetailsState.Success) return
        val task = currentState.task
        val taskId = task.id ?: return

        when (command) {
            TaskDetailsCommand.MarkAsCompleted -> {
                val newTask = task.copy(status = Status.DONE)
                optimisticUpdateAndSave(
                    newTask = newTask,
                    successMessage = "Task completed",
                    errorMessage = "Failed to complete task",
                    oldState = currentState
                )
            }
            TaskDetailsCommand.MarkAsRework -> {
                val newTask = task.copy(status = Status.TODO)
                optimisticUpdateAndSave(
                    newTask = newTask,
                    successMessage = "Task returned to work",
                    errorMessage = "Failed to return task",
                    oldState = currentState
                )
            }
            TaskDetailsCommand.DeclineTask -> {
                viewModelScope.launch {
                    try {
                        deleteTaskUseCase(taskId)
                        sendEffect(TaskDetailsEffect.ShowToast("Task declined"))
                        sendEffect(TaskDetailsEffect.NavigateBack)
                    } catch (e: Exception) {
                        sendEffect(TaskDetailsEffect.ShowError("Failed to decline task"))
                    }
                }
            }
            TaskDetailsCommand.NavigateBack -> {
                sendEffect(TaskDetailsEffect.NavigateBack)
            }
        }
    }

    private fun optimisticUpdateAndSave(
        newTask: Task,
        successMessage: String,
        errorMessage: String,
        oldState: TaskDetailsState.Success
    ) {
        _state.value = TaskDetailsState.Success(newTask)

        viewModelScope.launch {
            try {
                editTaskUseCase(newTask)
                sendEffect(TaskDetailsEffect.ShowToast(successMessage))
                sendEffect(TaskDetailsEffect.NavigateBack)
            } catch (e: Exception) {
                _state.value = oldState
                sendEffect(TaskDetailsEffect.ShowError(errorMessage))
            }
        }
    }

    private fun sendEffect(effect: TaskDetailsEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}

// Commands
sealed interface TaskDetailsCommand {
    object MarkAsCompleted : TaskDetailsCommand
    object MarkAsRework : TaskDetailsCommand
    object DeclineTask : TaskDetailsCommand
    object NavigateBack : TaskDetailsCommand
}

// States
sealed interface TaskDetailsState {
    object Loading : TaskDetailsState
    data class Success(val task: Task) : TaskDetailsState
    data class Error(val message: String) : TaskDetailsState
}

// Effects
sealed interface TaskDetailsEffect {
    data class ShowToast(val message: String) : TaskDetailsEffect
    data class ShowError(val message: String) : TaskDetailsEffect
    object NavigateBack : TaskDetailsEffect
}