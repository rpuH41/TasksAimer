package com.liulkovich.tasksaimer.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.liulkovich.tasksaimer.data.mapper.toDomain
import com.liulkovich.tasksaimer.data.mapper.toDto
import com.liulkovich.tasksaimer.data.remote.BoardDTO
import com.liulkovich.tasksaimer.domain.entity.Board
import com.liulkovich.tasksaimer.domain.repository.BoardRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BoardRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : BoardRepository {

    private val boardsCollection = firestore.collection("Boards")

    override fun getBoardsByUser(userId: String): Flow<List<Board>> = callbackFlow {
        val query = boardsCollection.whereArrayContains("members", userId)  // ← members!

        val subscription = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            snapshot?.let {
                val boards = it.documents.mapNotNull { doc ->
                    val dto = doc.toObject(BoardDTO::class.java) ?: return@mapNotNull null
                    dto.toDomain(doc.id)  // ← toDomain(doc.id), БЕЗ copy(id)
                }
                trySend(boards)
            }
        }

        awaitClose { subscription.remove() }
    }

    override suspend fun addBoard(board: Board) {
        val dto = board.toDto().copy(
            ownerId = board.ownerId,
            members = listOf(board.ownerId),
            titleLowercase = board.title.lowercase()
        )
        boardsCollection.add(dto).await()
    }

    override suspend fun deleteBoardById(boardId: String) {
        boardsCollection.document(boardId).delete().await()
    }

    override suspend fun editBoard(board: Board) {
        val boardId = board.id ?: throw IllegalArgumentException("Board ID cannot be null")
        val dto = board.toDto().copy(
            titleLowercase = board.title.lowercase()
        )
        boardsCollection.document(boardId).set(dto).await()
    }

    override fun searchBoardByTitle(title: String, userId: String): Flow<List<Board>> = callbackFlow {
        val query = boardsCollection
            .whereArrayContains("members", userId)  // ← members!
            .whereGreaterThanOrEqualTo("titleLowercase", title)
            .whereLessThanOrEqualTo("titleLowercase", title + "\uf8ff")

        val subscription = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            snapshot?.let {
                val boards = it.documents.mapNotNull { doc ->
                    val dto = doc.toObject(BoardDTO::class.java) ?: return@mapNotNull null
                    dto.toDomain(doc.id)
                }
                trySend(boards)
            }
        }

        awaitClose { subscription.remove() }
    }
}