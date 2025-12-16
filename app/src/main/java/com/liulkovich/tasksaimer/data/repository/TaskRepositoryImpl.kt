package com.liulkovich.tasksaimer.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.liulkovich.tasksaimer.data.mapper.toDomain
import com.liulkovich.tasksaimer.data.mapper.toDto
import com.liulkovich.tasksaimer.data.remote.TaskDTO
import com.liulkovich.tasksaimer.domain.entity.Task
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

            val ownerId = taskDtoToSave.ownerId
            val ownerRef = firestore.collection("users").document(ownerId)
            val ownerSnapshot = transaction.get(ownerRef)
            val ownerName = ownerSnapshot.getString("name")
                ?: ownerSnapshot.getString("email")
                ?: "Someone"

            val boardSnapshot = transaction.get(boardRef)
            val boardTitle = boardSnapshot.getString("title") ?: "Unknown board"

            if (taskDtoToSave.assignedTo.isNotEmpty()) {
                transaction.update(
                    boardRef,
                    "members",
                    FieldValue.arrayUnion(*taskDtoToSave.assignedTo.toTypedArray())
                )
            }

            transaction.set(newTaskRef, taskDtoToSave)

            val assigneesToNotify = taskDtoToSave.assignedTo.filter { it != ownerId }
            assigneesToNotify.forEach { assigneeId ->
                val notifRef = firestore.collection("users").document(assigneeId)
                    .collection("notifications").document()

                val notification = hashMapOf(
                    "type" to "TASK_ASSIGNED",
                    "title" to "New Task Assigned",
                    "message" to "$ownerName assigned you to \"${task.title}\" in board \"$boardTitle\"",
                    "taskId" to newTaskRef.id,
                    "boardId" to boardId,
                    "boardTitle" to boardTitle,
                    "fromUserId" to ownerId,
                    "timestamp" to FieldValue.serverTimestamp(),
                    "isRead" to false
                )
                transaction.set(notifRef, notification)
            }

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

    override suspend fun updateTaskAssigneesAndBoardMembers(
        taskId: String?,
        boardId: String,
        assigneeIds: List<String>
    ) {
        val boardRef = firestore.collection("Boards").document(boardId)

        firestore.runTransaction { transaction ->
            if (taskId != null) {
                val taskRef = tasksCollection.document(taskId)
                transaction.update(taskRef, "assignedTo", assigneeIds)
            }

            if (assigneeIds.isNotEmpty()) {
                transaction.update(
                    boardRef,
                    "members",
                    FieldValue.arrayUnion(*assigneeIds.toTypedArray())
                )
            }

        }.await()
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
        val taskId = task.id
            ?: throw IllegalArgumentException("Task ID cannot be null when editing a task.")

        val boardId = task.boardId
            ?: throw IllegalArgumentException("Task must have boardId when editing.")

        val ownerId = task.ownerId
            ?: throw IllegalStateException("Task must have ownerId")

        val taskDto = task.toDto().copy(
            id = taskId,
            ownerId = ownerId
        )

        val boardRef = firestore.collection("Boards").document(boardId)

        firestore.runTransaction { transaction ->
            val taskRef = tasksCollection.document(taskId)

            val currentSnapshot = transaction.get(taskRef)
            val currentAssignedTo = currentSnapshot.get("assignedTo") as? List<String> ?: emptyList()

            transaction.set(taskRef, taskDto)

            val newAssignees = task.assignedTo.filter { it !in currentAssignedTo }
            if (newAssignees.isNotEmpty()) {
                transaction.update(
                    boardRef,
                    "members",
                    FieldValue.arrayUnion(*newAssignees.toTypedArray())
                )
            }

            val removedAssignees = currentAssignedTo.filter { it !in task.assignedTo }
            if (removedAssignees.isNotEmpty()) {
                transaction.update(
                    boardRef,
                    "members",
                    FieldValue.arrayRemove(*removedAssignees.toTypedArray())
                )
            }

            val assigneesToNotify = newAssignees.filter { it != ownerId }

            if (assigneesToNotify.isNotEmpty()) {
                val ownerSnapshot = transaction.get(firestore.collection("users").document(ownerId))
                val ownerName = ownerSnapshot.getString("name")
                    ?: ownerSnapshot.getString("email")
                    ?: "Someone"

                val boardSnapshot = transaction.get(boardRef)
                val boardTitle = boardSnapshot.getString("title") ?: "Unknown board"

                assigneesToNotify.forEach { assigneeId ->
                    val notifRef = firestore.collection("users").document(assigneeId)
                        .collection("notifications").document()

                    val notification = hashMapOf(
                        "type" to "TASK_ASSIGNED",
                        "title" to "New Task Assigned",
                        "message" to "$ownerName assigned you to \"${task.title}\" in board \"$boardTitle\"",
                        "taskId" to taskId,
                        "boardId" to boardId,
                        "boardTitle" to boardTitle,
                        "fromUserId" to ownerId,
                        "timestamp" to FieldValue.serverTimestamp(),
                        "isRead" to false
                    )
                    transaction.set(notifRef, notification)
                }
            }
        }.await()
    }

    override suspend fun deleteTask(taskId: String) {
        val taskDoc = tasksCollection.document(taskId).get().await()
        val boardId = taskDoc.getString("boardId")
            ?: throw IllegalArgumentException("Task has no boardId")

        val boardRef = firestore.collection("Boards").document(boardId)

        firestore.runTransaction { transaction ->
            transaction.delete(tasksCollection.document(taskId))
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