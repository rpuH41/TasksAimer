package com.liulkovich.tasksaimer.presentation.screen.createboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liulkovich.tasksaimer.domain.entiity.Board
import com.liulkovich.tasksaimer.domain.usecase.board.AddBoardUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class CreateBoardViewModel @Inject constructor(
    private val addBoardUseCase: AddBoardUseCase
): ViewModel(){

    private val _state = MutableStateFlow<CreateBoardState>(CreateBoardState.Creation())
    val state = _state.asStateFlow()

    fun processCommand(command: CreateBoardCommand){

        when(command){
            is CreateBoardCommand.AddImage -> {
                _state.update { previousState ->
                    if (previousState is CreateBoardState.Creation) {
                        previousState.copy(
                            imageUrl = command.url.toString()
                        )
                    } else {
                        previousState
                    }
                }
            }
            is CreateBoardCommand.DeleteImage -> {
                _state.update { previousState ->
                    if (previousState is CreateBoardState.Creation) {
                        previousState.copy(imageUrl = null)
                    } else {
                        previousState
                    }
                }
            }
            is CreateBoardCommand.InputTitle -> {
                _state.update { previousState ->
                    if (previousState is CreateBoardState.Creation) {
                        previousState.copy(
                            title = command.title
                        )
                    } else {
                        previousState
                    }
                }
            }
            is CreateBoardCommand.InputDescription -> {
                _state.update {previousState ->
                    if (previousState is CreateBoardState.Creation) {
                        previousState.copy(
                            description = command.description
                        )
                    } else {
                        previousState
                    }

                }
            }
            CreateBoardCommand.Back -> {
                _state.update { CreateBoardState.Finished }
            }
            CreateBoardCommand.SaveBoard -> {
                viewModelScope.launch {
                    _state.update { previousState ->
                        if (previousState is CreateBoardState.Creation) {
                            val imageUrl = previousState.imageUrl
                            val title = previousState.title
                            val description = previousState.description
                            addBoardUseCase(
                                Board(
                                title = title,
                                description = description,
                                imageUrl = imageUrl,
                                ownerId = "currentUserId",
                                )
                            )
                            CreateBoardState.Finished
                        } else {
                            previousState
                        }
                    }
                }
            }
        }
    }
}

sealed interface CreateBoardCommand {

    data class AddImage(val url: URL) : CreateBoardCommand

    data class InputTitle(val title: String) : CreateBoardCommand

    data class InputDescription(val description: String) : CreateBoardCommand

    data class DeleteImage(val index: Int) : CreateBoardCommand

    data object SaveBoard  : CreateBoardCommand

    data object Back  : CreateBoardCommand
}

sealed interface CreateBoardState {

        data class Creation(
            val title: String = "",
            val description: String = "",
            val imageUrl: String? = null
        ): CreateBoardState {
            val isSaveEnabled: Boolean
                get() = title.isNotBlank() && imageUrl != null
        }

        data object Finished: CreateBoardState
}
