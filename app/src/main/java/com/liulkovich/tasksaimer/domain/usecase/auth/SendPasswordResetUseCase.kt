package com.liulkovich.tasksaimer.domain.usecase.auth

import com.liulkovich.tasksaimer.domain.repository.AuthRepository
import javax.inject.Inject

class SendPasswordResetUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String) = authRepository.sendPasswordReset(email)
}
