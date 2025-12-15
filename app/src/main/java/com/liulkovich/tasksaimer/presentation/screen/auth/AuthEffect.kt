package com.liulkovich.tasksaimer.presentation.screen.auth

sealed interface AuthEffect {
    object NavigateToHome : AuthEffect
    data class Message(val text: String) : AuthEffect
    data class ShowToast(val message: String) : AuthEffect
}