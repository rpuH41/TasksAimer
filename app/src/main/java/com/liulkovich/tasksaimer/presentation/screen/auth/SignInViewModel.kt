package com.liulkovich.tasksaimer.presentation.screen.auth

import com.liulkovich.tasksaimer.domain.usecase.auth.GetCurrentUserUseCase
import com.liulkovich.tasksaimer.domain.usecase.auth.SignInUseCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liulkovich.tasksaimer.domain.usecase.auth.SendPasswordResetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first // ADDED
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val sendPasswordResetUseCase: SendPasswordResetUseCase
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _isButtonEnabled = MutableStateFlow(false)
    val isButtonEnabled = _isButtonEnabled.asStateFlow()

    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state = _state.asStateFlow()

    private val _showResetDialog = MutableStateFlow(false)
    val showResetDialog = _showResetDialog.asStateFlow()

    private val _effect = Channel<AuthEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        checkCurrentUser() // stays
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
                onSuccess = {
                    sendEffect(AuthEffect.NavigateToHome)
                    _state.value = AuthState.Idle
                },
                onFailure = {
                    _state.value = AuthState.Error(it.message ?: "Sign in failed")
                }
            )
        }
    }

    private fun checkCurrentUser() {
        viewModelScope.launch {

            val user = getCurrentUserUseCase().first()

            if (user != null) {
                sendEffect(AuthEffect.NavigateToHome) // stays
            }
        }
    }

    private fun sendEffect(effect: AuthEffect) {
        viewModelScope.launch { _effect.send(effect) }
    }

    fun openResetDialog() { _showResetDialog.value = true } // ADDED
    fun closeResetDialog() { _showResetDialog.value = false } // ADDED

    fun resetPassword(email: String) {
        if (email.isBlank()) {
            viewModelScope.launch {
                sendEffect(AuthEffect.ShowToast("Введите email"))
            }
            return
        }

        viewModelScope.launch {
            _state.value = AuthState.Loading

            sendPasswordResetUseCase(email).fold(
                onSuccess = {
                    val toastMessage = """
                    Ссылка для сброса пароля отправлена на $email
                """.trimIndent()
                // Если письмо не пришло через минуту — загляните в папку «Спам» или «Реклама»
                    sendEffect(AuthEffect.ShowToast(toastMessage))
                    closeResetDialog()
                },
                onFailure = {
                    sendEffect(AuthEffect.ShowToast(it.message ?: "Не удалось отправить письмо"))
                }
            )
        }
    }
}
