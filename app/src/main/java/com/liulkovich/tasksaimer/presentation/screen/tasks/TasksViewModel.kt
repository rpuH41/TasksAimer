package com.liulkovich.tasksaimer.presentation.screen.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liulkovich.tasksaimer.domain.entiity.Task
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
                // Включаем загрузку при старте или при новом запросе
                _state.update { it.copy(isLoading = true, error = null) }
            }
            .flatMapLatest { (q, id) ->
                if (id == null) {
                    emptyFlow()
                } else if (q.isBlank()) {
                    //  Если ID есть и нет запроса, читаем все задачи для этой доски
                    getTasksForBoardUseCase(id)
                } else {
                    searchTaskByTitleUseCase(id,q)
                }
            }
            .catch { throwable ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = throwable.message ?: "Неизвестная ошибка загрузки задач."
                    )
                }
            }
            .onEach {tasks ->
                _state.update { it.copy(tasks = tasks, isLoading = false, error = null) }
            }
            .launchIn(viewModelScope)

    }

    fun processCommand(command: TaskCommand){
        viewModelScope.launch {
            when(command){
                is TaskCommand.FilterByStatus -> {
                    filterTasksByStatusUseCase(command.boardId, command.status)
                }

                is TaskCommand.InputSearchQuery -> {
                    query.update { command.query.trim() }
                }

                is TaskCommand.SetBoardId -> {
                    boardId.update { command.id }
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
    val tasks: List<Task> = listOf(),
    val isLoading: Boolean = true,
    val error: String? = null
)
