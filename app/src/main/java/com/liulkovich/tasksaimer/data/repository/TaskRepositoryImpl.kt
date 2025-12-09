package com.liulkovich.tasksaimer.data.repository

import com.google.firebase.firestore.FieldValue
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

                    val taskDto = document.toObject(TaskDTO::class.java)
                    taskDto?.copy(id = document.id)?.toDomain()
                }
                trySend(tasks)
            }
        }
        awaitClose {
            subscription.remove()
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

//    override suspend fun addTask(task: Task) {
//        val taskDto = task.toDto()
//        tasksCollection.add(taskDto).await()
//    }

    override suspend fun addTask(task: Task) {
        val boardId = task.boardId ?: throw IllegalArgumentException("Task must have boardId")
        val boardRef = firestore.collection("Boards").document(boardId)

        firestore.runTransaction { transaction ->
            val baseTaskDto = task.toDto()
            val newTaskRef = tasksCollection.document()

            val taskDtoToSave = baseTaskDto.copy(
                id = newTaskRef.id,
                boardId = boardId,
                titleLowercase = task.title.lowercase(),
                ownerId = task.ownerId
                    ?: com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
                    ?: throw IllegalStateException("User not authenticated")
            )

            transaction.set(newTaskRef, taskDtoToSave)

            transaction.update(boardRef, "tasksCount", FieldValue.increment(1))
        }.await()
    }

    override fun searchTaskByTitle(boardId: String, title: String): Flow<List<Task>> = callbackFlow {
        val query = tasksCollection
            .whereEqualTo("boardId",boardId)
            .whereGreaterThanOrEqualTo("titleLowercase", title)
            .whereLessThanOrEqualTo("titleLowercase", title + "\uf8ff")
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
        val taskDto = task.toDto().copy(
            ownerId = task.ownerId
        )
        tasksCollection.document(taskId).set(taskDto).await()
    }

//    override suspend fun deleteTask(taskId: String) {
//        tasksCollection.document(taskId)
//            .delete()
//            .await()
//    }

    override suspend fun deleteTask(taskId: String) {
        val taskDoc = tasksCollection.document(taskId).get().await()
        val boardId = taskDoc.getString("boardId")
            ?: throw IllegalArgumentException("Task has no boardId")

        val boardRef = firestore.collection("Boards").document(boardId)

        firestore.runTransaction { transaction ->
            // Удаляем задачу
            transaction.delete(tasksCollection.document(taskId))
            // Уменьшаем счётчик
            transaction.update(boardRef, "tasksCount", FieldValue.increment(-1))
        }.await()
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