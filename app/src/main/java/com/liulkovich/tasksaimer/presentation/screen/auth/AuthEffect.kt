package com.liulkovich.tasksaimer.presentation.screen.auth

sealed interface AuthEffect {
    object NavigateToHome : AuthEffect
}