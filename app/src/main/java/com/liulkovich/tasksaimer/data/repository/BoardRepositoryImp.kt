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

class BoardRepositoryImp @Inject constructor(
    private val firestore: FirebaseFirestore
): BoardRepository {

    private val boardsCollection = firestore.collection("boards")

    override fun getAllBoards(): Flow<List<Board>> = callbackFlow {
        val query = boardsCollection
        val subscription = query.addSnapshotListener { snapshot, error ->

            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val boards = snapshot.documents.mapNotNull { document ->
                    // Десериализация в DTO
                    val boardDto = document.toObject(BoardDTO::class.java)
                    boardDto?.copy(id = document.id)?.toDomain()
                }
                trySend(boards)
            }
        }
        awaitClose {
            subscription.remove() // Отменяем слушатель Firestore!
        }
    }

    override suspend fun addBoard(board: Board) {
        val boardDto = board.toDto()
        boardsCollection.add(boardDto).await()
    }

    override suspend fun deleteBoardById(boardId: String) {
        boardsCollection.document(boardId)
            .delete()
            .await()
    }

    override suspend fun editBoard(board: Board) {
        val boardDto = board.toDto()
        boardsCollection.document(board.id).set(boardDto).await()
    }

    override fun searchBoardByTitle(title: String): Flow<List<Board>> = callbackFlow {
        val query = boardsCollection
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
                    boardDto?.copy(id = document.id)?.toDomain()
                }
                trySend(boards)
            }
        }
        awaitClose {
            subscription.remove()
        }
    }
}