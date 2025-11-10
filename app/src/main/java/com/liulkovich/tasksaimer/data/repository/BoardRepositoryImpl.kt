package com.liulkovich.tasksaimer.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.liulkovich.tasksaimer.data.mapper.toDomain
import com.liulkovich.tasksaimer.data.mapper.toDto
import com.liulkovich.tasksaimer.data.remote.BoardDTO
import com.liulkovich.tasksaimer.domain.entiity.Board
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
): BoardRepository {

    private val boardsCollection = firestore.collection("Boards")

    override fun getBoardsByUser(userId: String): Flow<List<Board>> = callbackFlow {
        val query = boardsCollection.whereArrayContains("memberIds", userId)
        val subscription = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val boards = snapshot.documents.mapNotNull { document ->
                    val boardDto = document.toObject(BoardDTO::class.java)
                    boardDto?.copy(id = document.id)?.toDomain(document.id)
                }
                trySend(boards)
            }
        }
        awaitClose { subscription.remove() }
    }

    override suspend fun addBoard(board: Board) {
        val boardDto = board.toDto().copy(
            ownerId = board.ownerId,
            members = listOf(board.ownerId)
        )
        boardsCollection.add(boardDto).await()
    }

    override suspend fun deleteBoardById(boardId: String) {
        boardsCollection.document(boardId)
            .delete()
            .await()
    }

    override suspend fun editBoard(board: Board) {
        val boardId = board.id ?:
        throw IllegalArgumentException("Board ID cannot be null when editing a board.")
        val boardDto = board.toDto()
        boardsCollection.document(boardId).set(boardDto).await()
    }

    override fun searchBoardByTitle(title: String, userId: String): Flow<List<Board>> = callbackFlow {
        val query = boardsCollection
            .whereArrayContains("memberIds", userId)
            .whereGreaterThanOrEqualTo("title", title)
            .whereLessThanOrEqualTo("title", title + "\uf8ff")

        val subscription = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val boards = snapshot.documents.mapNotNull { document ->
                    val boardDto = document.toObject(BoardDTO::class.java)
                    boardDto?.copy(id = document.id)?.toDomain(document.id)
                }
                trySend(boards)
            }
        }
        awaitClose { subscription.remove() }
    }
}