package com.liulkovich.tasksaimer.domain.usecase.board

import com.liulkovich.tasksaimer.domain.entiity.Board
import com.liulkovich.tasksaimer.domain.repository.BoardRepository
import javax.inject.Inject

class EditBoardUseCase @Inject constructor(
    private val boardRepository: BoardRepository
){
    suspend operator fun invoke(board: Board) {
        boardRepository.editBoard(board)
    }
}