package com.liulkovich.tasksaimer.presentation.screen.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liulkovich.tasksaimer.domain.entity.Status
import com.liulkovich.tasksaimer.domain.entity.Task
import com.liulkovich.tasksaimer.domain.usecase.task.GetTasksForBoardUseCase
import com.liulkovich.tasksaimer.domain.usecase.task.SearchTaskByTitleUseCase
import com.liulkovich.tasksaimer.domain.usecase.task.FilterTasksByStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val getTasksForBoardUseCase: GetTasksForBoardUseCase,
    private val filterTasksByStatusUseCase: FilterTasksByStatusUseCase,
    private val searchTaskByTitleUseCase: SearchTaskByTitleUseCase
) : ViewModel(){

    private val query = MutableStateFlow("")
    private val boardId = MutableStateFlow<String?>(null)

    private val _state = MutableStateFlow(TaskState())
    val state = _state.asStateFlow()

    init {
        query.combine(boardId) { q, id -> Pair(q, id) }
            .onEach { (q, id) ->
                _state.update { it.copy(query = q) }
            }
            .onStart {
                _state.update { it.copy(isLoading = true, error = null) }
            }
            .flatMapLatest { (q, id) ->
                if (id == null) {
                    emptyFlow()
                } else if (q.isBlank()) {
                    getTasksForBoardUseCase(id)
                } else {
                    searchTaskByTitleUseCase(id,q.lowercase())
                }
            }
            .catch { throwable ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = throwable.message ?: "Unknown error loading tasks."
                    )
                }
            }
            .onEach { tasks ->
                _state.update {
                    it.copy(
                        originalTasks = tasks,
                        tasks = tasks,
                        isLoading = false,
                        error = null
                    )
                }
            }
            .launchIn(viewModelScope)

    }

    fun processCommand(command: TaskCommand){
        viewModelScope.launch {
            when(command){
                is TaskCommand.FilterByStatus -> {
                    val allTasks = _state.value.originalTasks

                    val filteredTasks = if (command.status.isBlank()) {
                        allTasks
                    } else {
                        try {
                            val statusEnum = Status.valueOf(command.status)
                            allTasks.filter { it.status == statusEnum }
                        } catch (e: Exception) {
                            allTasks
                        }
                    }

                    _state.update {
                        it.copy(
                            tasks = filteredTasks,
                            selectedFilter = if (command.status.isBlank()) null else command.status
                        )
                    }
                }

                is TaskCommand.InputSearchQuery -> {
                    query.update { command.query.trim() }
                    _state.update { it.copy(selectedFilter = null) }
                }

                is TaskCommand.SetBoardId -> {
                    boardId.update { command.id }
                    _state.update { it.copy(selectedFilter = null) }
                }
            }
        }
    }
}

sealed interface TaskCommand{

    data class FilterByStatus(val boardId: String, val status: String): TaskCommand

    data class InputSearchQuery(val query: String): TaskCommand

    data class SetBoardId(val id: String): TaskCommand
}

data class TaskState(
    val query: String = "",
    val originalTasks: List<Task> = emptyList(),
    val tasks: List<Task> = emptyList(),
    val selectedFilter: String? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)
