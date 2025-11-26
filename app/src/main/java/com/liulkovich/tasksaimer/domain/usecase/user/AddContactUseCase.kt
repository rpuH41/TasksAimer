package com.liulkovich.tasksaimer.domain.usecase.user

import com.liulkovich.tasksaimer.domain.entiity.User
import com.liulkovich.tasksaimer.domain.repository.UserRepository
import javax.inject.Inject

class AddContactUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String, contact: User): Result<Unit> =
        userRepository.addContact(userId, contact)
}