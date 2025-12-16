package com.liulkovich.tasksaimer.domain.usecase.auth

import com.liulkovich.tasksaimer.domain.entity.User
import com.liulkovich.tasksaimer.domain.repository.AuthRepository
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(user: User, password: String): Result<User> =
        authRepository.signUp(user, password)

}
