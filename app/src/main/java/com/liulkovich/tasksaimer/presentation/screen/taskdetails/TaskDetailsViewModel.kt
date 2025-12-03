package com.liulkovich.tasksaimer.presentation.screen.taskdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liulkovich.tasksaimer.domain.entiity.Status
import com.liulkovich.tasksaimer.domain.entiity.Task
import com.liulkovich.tasksaimer.domain.entiity.User
import com.liulkovich.tasksaimer.domain.usecase.task.DeleteTaskUseCase
import com.liulkovich.tasksaimer.domain.usecase.task.EditTaskUseCase
import com.liulkovich.tasksaimer.domain.usecase.task.GetTaskByIdUseCase
import com.liulkovich.tasksaimer.domain.usecase.user.GetUserByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
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
    private val getUserByIdUseCase: GetUserByIdUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val taskId: String = savedStateHandle["taskId"]
        ?: throw IllegalStateException("taskId is required")

    private val _state = MutableStateFlow<TaskDetailsState>(TaskDetailsState.Loading)
    val state = _state.asStateFlow()

    private val _effect = Channel<TaskDetailsEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        observeTask()
    }

    private fun observeTask() {
        getTaskByIdUseCase(taskId)
            .onStart { _state.value = TaskDetailsState.Loading }
            .catch { e ->
                _state.value = TaskDetailsState.Error(e.message ?: "Failed to load task")
            }
            .onEach { task ->
                if (task == null) {
                    _state.value = TaskDetailsState.Error("Task was deleted")
                    return@onEach
                }

                println("TASK OWNER ID: ${task.ownerId}")  //не забыть удалить
                println("TASK ASSIGNED TO: ${task.assignedTo}")

                _state.value = TaskDetailsState.Success(
                    task = task,
                    creator = null,
                    assignee = null
                )

                loadUsers(task)
            }
            .launchIn(viewModelScope)
    }

    private fun loadUsers(task: Task) {
        viewModelScope.launch {

            val creator = getUserByIdUseCase(task.ownerId)
                .catch { emit(null) }
                .first()

            val assigneeId = task.assignedTo.firstOrNull()

            val assignee = if (assigneeId != null) {
                getUserByIdUseCase(assigneeId)
                    .catch { emit(null) }
                    .first()
            } else null

            println("Creator loaded: $creator")
            println("Assignee loaded: $assignee")

            val currentTask = (state.value as? TaskDetailsState.Success)?.task ?: return@launch

            _state.value = TaskDetailsState.Success(
                task = currentTask,
                creator = creator,
                assignee = assignee
            )
        }
    }


    fun processCommand(command: TaskDetailsCommand) {
        val currentState = _state.value as? TaskDetailsState.Success ?: return
        val task = currentState.task
        val id = task.id ?: return

        when (command) {

            TaskDetailsCommand.MarkAsCompleted -> {
                updateTask(
                    newTask = task.copy(status = Status.DONE),
                    successMsg = "Task completed",
                    errorMsg = "Failed to complete task"
                )
            }

            TaskDetailsCommand.MarkAsRework -> {
                updateTask(
                    newTask = task.copy(status = Status.IN_PROGRESS),
                    successMsg = "Task returned to work",
                    errorMsg = "Failed to return task"
                )
            }

            TaskDetailsCommand.DeclineTask -> {
                declineTask(id)
            }

            TaskDetailsCommand.NavigateBack -> {
                sendEffect(TaskDetailsEffect.NavigateBack)
            }
        }
    }

    private fun updateTask(
        newTask: Task,
        successMsg: String,
        errorMsg: String
    ) {
        val oldState = _state.value as? TaskDetailsState.Success ?: return

        _state.value = oldState.copy(task = newTask)

        viewModelScope.launch {
            try {
                editTaskUseCase(newTask)
                sendEffect(TaskDetailsEffect.ShowToast(successMsg))
                sendEffect(TaskDetailsEffect.NavigateBack)
            } catch (e: Exception) {
                _state.value = oldState
                sendEffect(TaskDetailsEffect.ShowError(errorMsg))
            }
        }
    }

    private fun declineTask(taskId: String) {
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

    private fun sendEffect(effect: TaskDetailsEffect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}

sealed interface TaskDetailsState {
    object Loading : TaskDetailsState

    data class Success(
        val task: Task,
        val creator: User?,
        val assignee: User?
    ) : TaskDetailsState

    data class Error(val message: String) : TaskDetailsState
}

sealed interface TaskDetailsCommand {
    object MarkAsCompleted : TaskDetailsCommand
    object MarkAsRework : TaskDetailsCommand
    object DeclineTask : TaskDetailsCommand
    object NavigateBack : TaskDetailsCommand
}

sealed interface TaskDetailsEffect {
    data class ShowToast(val message: String) : TaskDetailsEffect
    data class ShowError(val message: String) : TaskDetailsEffect
    object NavigateBack : TaskDetailsEffect
}
