package com.liulkovich.tasksaimer.domain.usecase.user

import com.liulkovich.tasksaimer.domain.repository.UserRepository
import javax.inject.Inject

class RemoveContactUseCase@Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String, contactId: String): Result<Unit> =
        userRepository.removeContact(userId, contactId)
}