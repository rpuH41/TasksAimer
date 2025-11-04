package com.liulkovich.tasksaimer.presentation.screen.auth

sealed interface AuthState {
    object Idle : AuthState
    object Loading : AuthState
    data class Error(val message: String) : AuthState
}