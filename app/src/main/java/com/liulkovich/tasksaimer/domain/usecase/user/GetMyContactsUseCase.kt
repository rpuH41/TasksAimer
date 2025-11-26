package com.liulkovich.tasksaimer.domain.usecase.user

import com.liulkovich.tasksaimer.domain.entiity.User
import com.liulkovich.tasksaimer.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMyContactsUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(userId: String): Flow<List<User>> = userRepository.getMyContacts(userId)
}