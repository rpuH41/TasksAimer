package com.liulkovich.tasksaimer.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.liulkovich.tasksaimer.data.mapper.toDomain
import com.liulkovich.tasksaimer.data.mapper.toDto
import com.liulkovich.tasksaimer.data.remote.TaskDTO
import com.liulkovich.tasksaimer.domain.entiity.Task
import com.liulkovich.tasksaimer.domain.repository.TaskRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.jvm.java

class TaskRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : TaskRepository {

    private val tasksCollection = firestore.collection("tasks")

    override fun getTasksForBoard(boardId: String): Flow<List<Task>> = callbackFlow {
        val query = tasksCollection.whereEqualTo("boardId",boardId)
        val subscription = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val tasks = snapshot.documents.mapNotNull { document ->
                    // Десериализация в DTO
                    val taskDto = document.toObject(TaskDTO::class.java)
                    taskDto?.copy(id = document.id)?.toDomain()
                }
                trySend(tasks)
            }
        }
        awaitClose {
            subscription.remove() // Отменяем слушатель Firestore!
        }
    }

    override fun getTaskForId(taskId: String): Flow<Task?> = callbackFlow {
        val docRef = tasksCollection.document(taskId)
        val subscription = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val taskDto = snapshot.toObject(TaskDTO::class.java)
                val task = taskDto?.copy(id = snapshot.id)?.toDomain()
                trySend(task)
            } else {
                trySend(null)
            }
        }
        awaitClose { subscription.remove() }
    }

    override suspend fun addTask(task: Task) {
        val taskDto = task.toDto()
        tasksCollection.add(taskDto).await()
    }

    override fun searchTaskByTitle(boardId: String, title: String): Flow<List<Task>> = callbackFlow {
        val query = tasksCollection
            .whereEqualTo("boardId",boardId)
            .whereGreaterThanOrEqualTo("title", title)
            .whereLessThanOrEqualTo("title", title + "\uf8ff")
        val subscription = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val tasks = snapshot.documents.mapNotNull { document ->
                    val taskDTO = document.toObject(TaskDTO::class.java)
                    taskDTO?.copy(id = document.id)?.toDomain()
                }
                trySend(tasks)
            }
        }
        awaitClose {
            subscription.remove()
        }
    }

    override fun sortedStatus(
        boardId: String,
        status: String,
    ): Flow<List<Task>> = callbackFlow {
        val query = tasksCollection
            .whereEqualTo("boardId",boardId)
            .whereEqualTo("status", status)
        val subscription = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val task = snapshot.documents.mapNotNull { document ->
                    val taskDTO = document.toObject(TaskDTO::class.java)
                    taskDTO?.copy(id = document.id)?.toDomain()
                }
                trySend(task)
            }
        }
        awaitClose {
            subscription.remove()
        }
    }

    override suspend fun editTask(task: Task) {
        val taskId = task.id ?:
        throw IllegalArgumentException("Task ID cannot be null when editing a board.")
        val taskDto = task.toDto()
        tasksCollection.document(taskId).set(taskDto).await()
    }

    override suspend fun deleteTask(taskId: String) {
        tasksCollection.document(taskId)
            .delete()
            .await()
    }

    override suspend fun deleteAllTasksByBoardId(boardId: String) {
       val snapshot = tasksCollection
            .whereEqualTo("boardId",boardId)
            .get()
            .await()
        for (document in snapshot.documents){
            tasksCollection.document(document.id)
                .delete()
                .await()
        }

    }


}