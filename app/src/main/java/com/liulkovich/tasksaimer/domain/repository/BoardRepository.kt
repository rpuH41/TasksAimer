package com.liulkovich.tasksaimer.domain.repository

import com.liulkovich.tasksaimer.domain.entiity.Board
import kotlinx.coroutines.flow.Flow

interface BoardRepository {

    fun getAllBoards(): Flow<List<Board>> //Отображает весь список досок

    suspend fun addBoard(board: Board) //добавление новой доски

    suspend fun deleteBoardById(boardId: String) //удаление доски

    suspend fun editBoard(board: Board) //редактирование доски

    fun searchBoardByTitle(title: String): Flow<List<Board>> //поиск по зоголовку

    //fun getSortedBoards(): Flow<List<Board>> в будущем релизовать

}