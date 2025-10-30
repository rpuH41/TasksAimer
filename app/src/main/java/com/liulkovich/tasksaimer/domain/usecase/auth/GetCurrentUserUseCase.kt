package com.liulkovich.tasksaimer.domain.usecase.auth

import com.liulkovich.tasksaimer.domain.entiity.User
import com.liulkovich.tasksaimer.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
){
    operator fun invoke(): Flow<User?> = authRepository.getCurrentUser()
}