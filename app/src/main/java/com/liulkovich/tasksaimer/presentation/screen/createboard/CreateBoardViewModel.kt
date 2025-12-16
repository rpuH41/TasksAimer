package com.liulkovich.tasksaimer.presentation.screen.createboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liulkovich.tasksaimer.domain.entity.Board
import com.liulkovich.tasksaimer.domain.usecase.auth.GetCurrentUserUseCase
import com.liulkovich.tasksaimer.domain.usecase.board.AddBoardUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
    class CreateBoardViewModel @Inject constructor(
        private val addBoardUseCase: AddBoardUseCase,
        private val getCurrentUserUseCase: GetCurrentUserUseCase
    ) : ViewModel() {

        private val _state = MutableStateFlow<CreateBoardState>(CreateBoardState.Creation())
        val state = _state.asStateFlow()

        fun processCommand(command: CreateBoardCommand) {

            /* is CreateBoardCommand.AddImage -> {
                _state.update { previousState ->
                    if (previousState is CreateBoardState.Creation) {
                        previousState.copy(
                            imageUrl = command.url.toString()
                        )
                    } else {
                        previousState
                    }
                }
            }*/
            /*is CreateBoardCommand.DeleteImage -> {
                _state.update { previousState ->
                    if (previousState is CreateBoardState.Creation) {
                        previousState.copy(imageUrl = null)
                    } else {
                        previousState
                    }
                }
            }*/
            when (command) {
                is CreateBoardCommand.InputTitle -> {
                    updateState { copy(title = command.title) }
                }
                is CreateBoardCommand.InputDescription -> {
                    updateState { copy(description = command.description) }
                }
                CreateBoardCommand.SaveBoard -> saveBoard()
                CreateBoardCommand.Back -> {
                    _state.value = CreateBoardState.Finished
                }
            }
        }

        private fun updateState(block: CreateBoardState.Creation.() -> CreateBoardState.Creation) {
            _state.update { previous ->
                if (previous is CreateBoardState.Creation) block(previous) else previous
            }
        }

    private fun saveBoard() {
        viewModelScope.launch {
            val creation = _state.value as? CreateBoardState.Creation ?: return@launch

            val userId = getCurrentUserUseCase().firstOrNull() ?: return@launch

            addBoardUseCase(
                Board(
                    id = null,
                    title = creation.title,
                    description = creation.description,
                    ownerId = userId,
                    members = listOf(userId)
                )
            )

            _state.value = CreateBoardState.Finished  // ← Только после сохранения!
        }
    }
    }



sealed interface CreateBoardCommand {

    //data class AddImage(val url: URL) : CreateBoardCommand

    data class InputTitle(val title: String) : CreateBoardCommand

    data class InputDescription(val description: String) : CreateBoardCommand

    //data class DeleteImage(val index: Int) : CreateBoardCommand

    data object SaveBoard  : CreateBoardCommand

    data object Back  : CreateBoardCommand
}

sealed interface CreateBoardState {

        data class Creation(
            val title: String = "",
            val description: String = "",
            //val imageUrl: String? = null
        ): CreateBoardState {
            val isSaveEnabled: Boolean
                get() = title.isNotBlank() //&& imageUrl != null
        }

        data object Finished: CreateBoardState
}
