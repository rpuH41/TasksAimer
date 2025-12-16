package com.liulkovich.tasksaimer.domain.usecase.user

import com.liulkovich.tasksaimer.domain.entity.User
import com.liulkovich.tasksaimer.domain.repository.UserRepository
import javax.inject.Inject

class UpdateUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(user: User) {
        userRepository.updateUser(user)
    }
}