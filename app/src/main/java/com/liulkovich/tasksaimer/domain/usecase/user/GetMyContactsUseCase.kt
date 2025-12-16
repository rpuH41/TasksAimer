package com.liulkovich.tasksaimer.domain.usecase.user

import com.google.firebase.auth.FirebaseAuth
import com.liulkovich.tasksaimer.domain.entity.User
import com.liulkovich.tasksaimer.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetMyContactsUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<List<User>> = flow {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
            ?: run {
                emit(emptyList())
                return@flow
            }

        userRepository.getMyContacts(currentUserId)
            .collect { contacts ->
                emit(contacts)
            }
    }
    //operator fun invoke(userId: String): Flow<List<User>> = userRepository.getMyContacts(userId)
}