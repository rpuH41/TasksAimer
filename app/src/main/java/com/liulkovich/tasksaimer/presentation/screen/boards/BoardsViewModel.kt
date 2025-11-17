package com.liulkovich.tasksaimer.presentation.screen.boards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liulkovich.tasksaimer.domain.entiity.Board
import com.liulkovich.tasksaimer.domain.usecase.auth.GetCurrentUserUseCase
import com.liulkovich.tasksaimer.domain.usecase.board.GetBoardsUseCase
import com.liulkovich.tasksaimer.domain.usecase.board.SearchBoardByTitleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BoardsViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getBoardsUseCase: GetBoardsUseCase,
    private val searchBoardByTitleUseCase: SearchBoardByTitleUseCase
) : ViewModel() {

    private val userIdFlow = getCurrentUserUseCase()
        .catch { emit(null) }
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(5000), replay = 1)

    private val searchQuery = MutableStateFlow("")

    private val _state = MutableStateFlow(BoardState())
    val state = _state.asStateFlow()

    init {
        userIdFlow
            .flatMapLatest { userId ->
                if (userId == null) {
                    flowOf(emptyList<Board>())
                } else {
                    searchQuery
                        .debounce(300)
                        .distinctUntilChanged()
                        .flatMapLatest { query ->
                            if (query.isBlank()) {
                                getBoardsUseCase(userId)
                            } else {
                                searchBoardByTitleUseCase(query.lowercase(), userId)
                            }
                        }
                }
            }
            .onStart { _state.update { it.copy(isLoading = true) } } // лоадер только один раз!
            .catch { throwable ->
                _state.update {
                    it.copy(isLoading = false, error = throwable.message ?: "Error loading boards")
                }
            }
            .onEach { boards ->
                _state.update {
                    it.copy(
                        boards = boards,
                        isLoading = false,
                        error = null
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun processCommand(command: BoardsCommand) {
        when (command) {
            is BoardsCommand.InputSearchQuery -> {
                searchQuery.value = command.query
                _state.update { it.copy(query = command.query) }
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