package com.liulkovich.tasksaimer.domain.repository

import com.liulkovich.tasksaimer.domain.entiity.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    suspend fun signIn(email: String, password: String): Result<User>

    suspend fun signUp(user: User, password: String): Result<User>

    suspend fun logout(): Result<Unit>

    fun getCurrentUser(): Flow<User?>
}