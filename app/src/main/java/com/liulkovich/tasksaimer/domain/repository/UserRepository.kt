package com.liulkovich.tasksaimer.domain.repository

import com.liulkovich.tasksaimer.domain.entiity.User
import kotlinx.coroutines.flow.Flow
import java.net.URI

interface UserRepository {

    fun getUser(userId: String): Flow<User>

    suspend fun updateUser(user: User): Result<Unit>

}