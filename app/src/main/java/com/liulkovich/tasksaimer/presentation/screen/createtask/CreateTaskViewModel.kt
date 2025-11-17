package com.liulkovich.tasksaimer.presentation.screen.createtask

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liulkovich.tasksaimer.domain.entiity.Priority
import com.liulkovich.tasksaimer.domain.entiity.Status
import com.liulkovich.tasksaimer.domain.entiity.Task
import com.liulkovich.tasksaimer.domain.interactor.DateInputInteractor
import com.liulkovich.tasksaimer.domain.usecase.task.AddTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateTaskViewModel @Inject constructor(
    private val addTaskUseCase: AddTaskUseCase,
    private val dateInputInteractor: DateInputInteractor,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    // Получаем boardId
    private val currentBoardId: String = savedStateHandle.get<String>("boardId")
        ?: throw IllegalStateException("boardId is required for CreateTaskViewModel.")

    private val _state = MutableStateFlow<CreateTaskState>(
        CreateTaskState.Creation(boardId = currentBoardId, title = ""))
    val state = _state.asStateFlow()

    fun processCommand(command: CreateTaskCommand) {
        when(command){
            is CreateTaskCommand.TitleTask -> {
                _state.update{ previousState ->
                    if (previousState is CreateTaskState.Creation) {
                        previousState.copy(
                            title = command.title
                        )
                    } else {
                        previousState
                    }
                }
            }
            is CreateTaskCommand.DescriptionTask -> {
                _state.update{ previousState ->
                    if (previousState is CreateTaskState.Creation) {
                        previousState.copy(description = command.description)
                    } else {
                        previousState
                    }
                }
            }

            is CreateTaskCommand.DueDate -> {
                val formatted = dateInputInteractor.formatUserInput(command.dueDate)
                val isValid = dateInputInteractor.isValid(formatted)
                _state.update {
                    if (it is CreateTaskState.Creation) {
                        it.copy(
                            dueDate = formatted,
                            dueDateError = if (isValid) null else "Invalid date"
                        )
                    } else it
                }
            }
            is CreateTaskCommand.DueTime -> {
                _state.update{ previousState ->
                    if (previousState is CreateTaskState.Creation) {
                        previousState.copy(dueTime = command.time)
                    } else {
                        previousState
                    }
                }
            }
            is CreateTaskCommand.SetPriority -> {
                _state.update{ previousState ->
                    if (previousState is CreateTaskState.Creation) {
                        previousState.copy(priority = command.priority)
                    } else {
                        previousState
                    }
                }
            }
            is CreateTaskCommand.AssignMembers -> {
                _state.update{ previousState ->
                    if (previousState is CreateTaskState.Creation) {
                        previousState.copy(assignedTo = command.assignedToIds)
                    } else {
                        previousState
                    }
                }
            }

            CreateTaskCommand.Back -> {
                _state.update{ CreateTaskState.Finished }
            }
            CreateTaskCommand.SaveTask -> {
                viewModelScope.launch {
                    val currentState = _state.value
                    if (currentState is CreateTaskState.Creation && currentState.isSaveEnabled) {

                        _state.update { CreateTaskState.Loading } //  показываем индикатор

                        val currentUserId = "user123"
                        val taskToSave = Task(
                            id = null,
                            boardId = currentState.boardId,
                            title = currentState.title,
                            description = currentState.description,
                            dueDate = currentState.dueDate,
                            dueTime = currentState.dueTime,
                            priority = currentState.priority,
                            status = currentState.status,
                            assignedTo = currentState.assignedTo,
                            ownerId = currentUserId
                        )

                        addTaskUseCase(taskToSave)
                        _state.update { CreateTaskState.Finished } // ✅ завершили
                    }
                }
            }
        }
    }
}

sealed interface CreateTaskCommand {

    data class TitleTask(val title: String): CreateTaskCommand

    data class DescriptionTask(val description: String): CreateTaskCommand

    data class DueDate(val dueDate: String): CreateTaskCommand

    data class DueTime(val time: String): CreateTaskCommand

    data class SetPriority(val priority: Priority): CreateTaskCommand

    data class AssignMembers(val assignedToIds: List<String>): CreateTaskCommand

    data object Back: CreateTaskCommand

    data object SaveTask: CreateTaskCommand
}

sealed interface CreateTaskState{
    data class Creation(
        val boardId: String,
        val title: String,
        val description: String? = null,
        val dueDate: String? = null,
        val dueDateError: String? = null,
        val dueTime: String? = null,
        val priority: Priority = Priority.MEDIUM,
        val status: Status = Status.TODO,
        val assignedTo: List<String> = emptyList(),
    ): CreateTaskState {
        val isSaveEnabled: Boolean
            get() = title.isNotBlank() && dueDateError == null
    }
    data object Loading : CreateTaskState

    data object Finished: CreateTaskState
}