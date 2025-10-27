package com.liulkovich.tasksaimer.presentation.screen.boards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liulkovich.tasksaimer.domain.entiity.Board
import com.liulkovich.tasksaimer.domain.usecase.board.GetBoardsUseCase
import com.liulkovich.tasksaimer.domain.usecase.board.SearchBoardByTitleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BoardsViewModel @Inject constructor(
    private val getAllBoardsUseCase: GetBoardsUseCase,
    private val searchBoardByTitleUseCase: SearchBoardByTitleUseCase
): ViewModel() {

    private val query = MutableStateFlow("")

    private val _state = MutableStateFlow(BoardState())
    val state = _state.asStateFlow()

    init {
        query
            .onEach { input ->
                _state.update { it.copy(query = input) }
            }
            .onStart {
            _state.update { it.copy(isLoading = true, error = null) }
            }
            .flatMapLatest { input ->
                if (input.isBlank()) {
                    getAllBoardsUseCase()
                } else {
                    searchBoardByTitleUseCase(input)
                }
            }
            .catch { throwable ->
                // Ловим ошибки, отключаем загрузку и записываем сообщение
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = throwable.message ?: "Unknown error loading boards."
                    )
                }
            }
            .onEach {boards ->
                _state.update { it.copy(boards = boards, isLoading = false, error = null) }
            }
            .launchIn(viewModelScope)
    }

    fun processCommand(command: BoardsCommand) {
        viewModelScope.launch {
                when(command) {
                    is BoardsCommand.InputSearchQuery -> {
                        query.update { command.query.trim() }
                    }
                }
            }
        }
}

sealed interface BoardsCommand {

    data class InputSearchQuery(val query: String): BoardsCommand
}

data class BoardState(
    val query: String = "",
    val boards: List<Board> = listOf(),
    val isLoading: Boolean = true,
    val error: String? = null
)