package com.liulkovich.tasksaimer.presentation.screen.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liulkovich.tasksaimer.domain.entiity.Board
import com.liulkovich.tasksaimer.domain.entiity.User
import com.liulkovich.tasksaimer.domain.usecase.auth.GetCurrentUserUseCase
import com.liulkovich.tasksaimer.domain.usecase.auth.LogoutUseCase
import com.liulkovich.tasksaimer.domain.usecase.board.GetBoardsUseCase
import com.liulkovich.tasksaimer.domain.usecase.user.AddContactUseCase
import com.liulkovich.tasksaimer.domain.usecase.user.GetAllUserUseCase
import com.liulkovich.tasksaimer.domain.usecase.user.GetMyContactsUseCase
import com.liulkovich.tasksaimer.domain.usecase.user.GetUserByIdUseCase
import com.liulkovich.tasksaimer.domain.usecase.user.RemoveContactUseCase
import com.liulkovich.tasksaimer.domain.usecase.user.UpdateUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getMyContactsUseCase: GetMyContactsUseCase,
    private val addContactUseCase: AddContactUseCase,
    private val removeContactUseCase: RemoveContactUseCase,
    private val getBoardsUseCase: GetBoardsUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val getAllUserUseCase: GetAllUserUseCase
    ): ViewModel(){
        private val _state = MutableStateFlow(ProfileScreenState())
        val state = _state.asStateFlow()

    init {
        observeCurrentUser()
        observeAllUsers()
    }

    private fun observeCurrentUser() {
        viewModelScope.launch {
            getCurrentUserUseCase().collect { userId ->

                if (userId == null) {
                    _state.value = _state.value.copy(
                        currentUserId = null,
                        contacts = emptyList(),
                        boards = emptyList()
                    )
                    return@collect
                }

                _state.value = _state.value.copy(
                    currentUserId = userId
                )
                observeUser(userId)
                observeContacts(userId)
                observeBoards(userId)
            }
        }
    }

    private fun observeUser(userId: String) {
        viewModelScope.launch {
            getUserByIdUseCase(userId).collect { user ->
                _state.value = _state.value.copy(currentUser = user)
            }
        }
    }

    private fun observeContacts(userId: String) {
        viewModelScope.launch {
            getMyContactsUseCase(userId).collect { contacts ->
                Log.d("PROFILE", "Contacts received: $contacts")
                _state.value = _state.value.copy(contacts = contacts)
            }
        }
    }

    private fun observeAllUsers() {
        viewModelScope.launch {
            getAllUserUseCase().collect { users ->
                _state.value = _state.value.copy(allUsers = users)
            }
        }
    }

    private fun observeBoards(userId: String) {
        viewModelScope.launch {
            getBoardsUseCase(userId).collect { boards ->
                Log.d("BOARDS", "Boards received: $boards")
                _state.value = _state.value.copy(boards = boards)
            }
        }
    }

    fun processCommand(command: ProfileCommand) {
        when (command) {
            is ProfileCommand.AddContact -> {
                viewModelScope.launch {
                    addContactUseCase(command.userId,command.contact)
                }

            }

            ProfileCommand.Logout -> {
                viewModelScope.launch {
                    logoutUseCase()
                    _state.value = _state.value.copy(isLoggedOut = true)
                }
            }

            is ProfileCommand.RemoveContact -> {
                viewModelScope.launch {
                    removeContactUseCase(command.userId, command.contactId)
                }

            }

            is ProfileCommand.OpenBoard -> {

            }

            is ProfileCommand.UpdateUser -> {
                viewModelScope.launch {
                    updateUserUseCase(command.user)
                }

            }
        }
    }
}

sealed interface ProfileCommand {

    data class OpenBoard(val boardId: String) : ProfileCommand

    data class AddContact(val userId: String, val contact: User) : ProfileCommand

    data class RemoveContact(val userId: String, val contactId: String) : ProfileCommand

    data class UpdateUser(val user: User) : ProfileCommand

    object Logout : ProfileCommand

}

data class ProfileScreenState(
    val currentUser: User? = null,
    val currentUserId: String? = null,
    val allUsers: List<User> = emptyList(),
    val contacts: List<User> = emptyList(),
    val boards: List<Board> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoggedOut: Boolean = false
)