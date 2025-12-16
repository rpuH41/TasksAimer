package com.liulkovich.tasksaimer.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.liulkovich.tasksaimer.domain.entity.User
import com.liulkovich.tasksaimer.domain.repository.UserRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.jvm.java

class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore

) : UserRepository {

    private val usersCollection = firestore.collection("users")

    //get all user for search and add
    override fun getUserByEmail(userEmail: String): Flow<User?> = callbackFlow {
        val listenerRegistration = usersCollection
            .whereEqualTo("email", userEmail)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val user = snapshot?.documents?.firstOrNull()?.toObject(User::class.java)
                trySend(user)
            }
        awaitClose { listenerRegistration.remove() }
    }

    //Change data user
    override suspend fun updateUser(user: User): Result<Unit> {
        return try {
            usersCollection.document(user.id).set(user).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    //add user to collection
    override suspend fun addContact(userId: String, contact: User): Result<Unit> {
        return try {
            usersCollection.document(userId)
                .collection("contacts")
                .document(contact.id)
                .set(contact)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    //remove user from collection
    override suspend fun removeContact(userId: String, contactId: String): Result<Unit> {
        return try {
            usersCollection.document(userId)
                .collection("contacts")
                .document(contactId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    //all my contact
    override fun getMyContacts(userId: String): Flow<List<User>> = callbackFlow {
        val listenerRegistration = firestore.collection("users")
            .document(userId)
            .collection("contacts")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val contacts = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(User::class.java)?.copy(id = doc.id) // ← ВАЖНО: добавляем id!
                } ?: emptyList()
                trySend(contacts)
            }

        awaitClose { listenerRegistration.remove() }
    }

    override fun getUserById(userId: String): Flow<User?> = callbackFlow {
        val listener = usersCollection
            .document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error);
                    return@addSnapshotListener
                }
                val user = snapshot?.toObject(User::class.java)?.copy(id = userId)
                trySend(user)
            }
        awaitClose { listener.remove() }
    }
    override fun getAllUsers(): Flow<List<User>> = callbackFlow {
        val listenerRegistration = usersCollection
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val users = snapshot?.documents
                    ?.mapNotNull { it.toObject(User::class.java) }
                    ?: emptyList()

                trySend(users)
            }

        awaitClose { listenerRegistration.remove() }
    }

}