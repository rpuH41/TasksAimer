package com.liulkovich.tasksaimer.data.repository

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.liulkovich.tasksaimer.domain.entiity.User
import com.liulkovich.tasksaimer.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore

): AuthRepository {

    private val auth = Firebase.auth
    private val users = firestore.collection("users")

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override suspend fun signUp(user: User, password: String): Result<User> = try {
        val authResult = auth.createUserWithEmailAndPassword(user.email, password).await()
        val uid = authResult.user!!.uid
        val fullUser = user.copy(id = uid)
        users.document(uid).set(fullUser).await()
        Result.success(fullUser)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun signIn(email: String, password: String): Result<User> = try {
        val authResult = auth.signInWithEmailAndPassword(email, password).await()
        val uid = authResult.user!!.uid
        val snapshot = users.document(uid).get().await()
        val user = snapshot.toObject(User::class.java)
            ?: return Result.failure(Exception("Profile not found"))
        Result.success(user.copy(id = uid))
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun logout(): Result<Unit> = try {
        auth.signOut()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override fun getCurrentUser(): Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null) {
                scope.launch {
                    try {
                        val snapshot = users.document(firebaseUser.uid).get().await()
                        val user = snapshot.toObject(User::class.java)
                        trySend(user?.copy(id = firebaseUser.uid))
                    } catch (e: Exception) {
                        trySend(null)
                    }
                }
            } else {
                trySend(null)
            }
        }
        auth.addAuthStateListener(listener)
        awaitClose {
            auth.removeAuthStateListener(listener)
            scope.cancel() // отмена при закрытии
        }
    }.flowOn(Dispatchers.IO)
}
