package com.liulkovich.tasksaimer.presentation.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liulkovich.tasksaimer.domain.entity.User
import com.liulkovich.tasksaimer.domain.usecase.auth.GetCurrentUserUseCase
import com.liulkovich.tasksaimer.domain.usecase.auth.SignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _firstName = MutableStateFlow("")
    val firstName = _firstName.asStateFlow()

    private val _lastName = MutableStateFlow("")
    val lastName = _lastName.asStateFlow()

    private val _newEmail = MutableStateFlow("")
    val newEmail = _newEmail.asStateFlow()

    private val _newPassword = MutableStateFlow("")
    val newPassword = _newPassword.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword = _confirmPassword.asStateFlow()

    private val _isButtonEnabled = MutableStateFlow(false)
    val isButtonEnabled = _isButtonEnabled.asStateFlow()

    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state = _state.asStateFlow()

    private val _effect = Channel<AuthEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        checkCurrentUser()
    }

    fun onFirstNameChanged(name: String) {
        _firstName.value = name
        validateForm()
    }

    fun onLastNameChanged(name: String) {
        _lastName.value = name
        validateForm()
    }

    fun onEmailChanged(email: String) {
        _newEmail.value = email
        validateForm()
    }

    fun onPasswordChanged(password: String) {
        _newPassword.value = password
        validateForm()
    }

    fun onConfirmPasswordChanged(confirm: String) {
        _confirmPassword.value = confirm
        validateForm()
    }

    private fun validateForm() {
        val isValid = _firstName.value.isNotBlank()
                && _lastName.value.isNotBlank()
                && _newEmail.value.contains("@")
                && _newPassword.value.length >= 6
                && _newPassword.value == _confirmPassword.value
        _isButtonEnabled.value = isValid
    }

    fun signUp() {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            val user = User(
                firstName = _firstName.value,
                lastName = _lastName.value,
                email = _newEmail.value
            )
            signUpUseCase(user, _newPassword.value).fold(
                onSuccess = { sendEffect(AuthEffect.NavigateToHome) },
                onFailure = { _state.value = AuthState.Error(it.message ?: "Sign up failed") }
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



