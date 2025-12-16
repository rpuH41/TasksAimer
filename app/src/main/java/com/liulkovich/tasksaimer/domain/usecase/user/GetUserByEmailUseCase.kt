package com.liulkovich.tasksaimer.domain.usecase.user

import com.liulkovich.tasksaimer.domain.entity.User
import com.liulkovich.tasksaimer.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserByEmailUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(userEmail: String): Flow<User?> =
        userRepository.getUserByEmail(userEmail)
}