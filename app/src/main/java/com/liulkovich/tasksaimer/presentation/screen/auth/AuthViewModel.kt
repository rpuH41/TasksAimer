package com.liulkovich.tasksaimer.presentation.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liulkovich.tasksaimer.domain.entiity.User
import com.liulkovich.tasksaimer.domain.usecase.auth.GetCurrentUserUseCase
import com.liulkovich.tasksaimer.domain.usecase.auth.SignInUseCase
import com.liulkovich.tasksaimer.domain.usecase.auth.SignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase,
    private val signUpUseCase: SignUpUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state = _state.asStateFlow()

    private val _effect = Channel<AuthEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        viewModelScope.launch {
            getCurrentUserUseCase().collect { user ->
                if (user != null) {
                    sendEffect(AuthEffect.NavigateToHome)
                }
            }
        }
    }

    fun processCommand(command: AuthCommand) {
        when (command) {
            is AuthCommand.SignIn -> signIn(command.email, command.password)
            is AuthCommand.SignUp -> signUp(command.user, command.password)
        }
    }

    private fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            signInUseCase(email, password).fold(
                onSuccess = { sendEffect(AuthEffect.NavigateToHome) },
                onFailure = { _state.value = AuthState.Error(it.message ?: "Sign in failed") }
            )
        }
    }

    private fun signUp(user: User, password: String) {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            signUpUseCase(user, password).fold(
                onSuccess = { sendEffect(AuthEffect.NavigateToHome) },
                onFailure = { _state.value = AuthState.Error(it.message ?: "Sign up failed") }
            )
        }
    }

    private fun sendEffect(effect: AuthEffect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}

sealed interface AuthState {
    object Idle : AuthState
    object Loading : AuthState
    data class Error(val message: String) : AuthState
}

sealed interface AuthCommand {
    data class SignIn(val email: String, val password: String) : AuthCommand
    data class SignUp(val user: User, val password: String) : AuthCommand
}

sealed interface AuthEffect {
    object NavigateToHome : AuthEffect
}
