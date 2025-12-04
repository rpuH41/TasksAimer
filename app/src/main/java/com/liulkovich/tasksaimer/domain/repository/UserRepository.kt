package com.liulkovich.tasksaimer.domain.repository

import com.liulkovich.tasksaimer.domain.entiity.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun getUserByEmail(userEmail: String): Flow<User?>

    suspend fun updateUser(user: User): Result<Unit>

    suspend fun addContact(userId: String, contact: User): Result<Unit>

    suspend fun removeContact(userId: String, contactId: String): Result<Unit>

    fun getMyContacts(userId: String): Flow<List<User>>

    fun getUserById(userId: String): Flow<User?>

    fun getAllUsers(): Flow<List<User>>


}