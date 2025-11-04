package com.liulkovich.tasksaimer.presentation.screen.auth

import com.liulkovich.tasksaimer.domain.usecase.auth.GetCurrentUserUseCase
import com.liulkovich.tasksaimer.domain.usecase.auth.SignInUseCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _isButtonEnabled = MutableStateFlow(false)
    val isButtonEnabled = _isButtonEnabled.asStateFlow()

    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state = _state.asStateFlow()

    private val _effect = Channel<AuthEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        checkCurrentUser()
    }

    fun onEmailChanged(email: String) {
        _email.value = email
        validateForm()
    }

    fun onPasswordChanged(password: String) {
        _password.value = password
        validateForm()
    }

    private fun validateForm() {
        val isValid = _email.value.isNotBlank()
                && _email.value.contains("@")
                && _password.value.length >= 6
        _isButtonEnabled.value = isValid
    }

    fun signIn() {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            signInUseCase(_email.value, _password.value).fold(
                onSuccess = { sendEffect(AuthEffect.NavigateToHome) },
                onFailure = { _state.value = AuthState.Error(it.message ?: "Sign in failed") }
            )
        }
    }

    private fun checkCurrentUser() {
        viewModelScope.launch {
            getCurrentUserUseCase().collect { user ->
                if (user != null) sendEffect(AuthEffect.NavigateToHome)
            }
        }
    }

    private fun sendEffect(effect: AuthEffect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}
