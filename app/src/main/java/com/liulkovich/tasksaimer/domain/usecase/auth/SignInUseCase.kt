package com.liulkovich.tasksaimer.domain.usecase.auth

import com.liulkovich.tasksaimer.domain.entity.User
import com.liulkovich.tasksaimer.domain.repository.AuthRepository
import javax.inject.Inject

class SignInUseCase @Inject constructor(
        private val authRepository: AuthRepository
) {
        suspend operator fun invoke(email: String, password: String): Result<User> =
            authRepository.signIn(email, password)

}
