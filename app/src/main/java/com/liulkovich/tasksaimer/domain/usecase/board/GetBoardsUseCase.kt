package com.liulkovich.tasksaimer.domain.usecase.board

import com.liulkovich.tasksaimer.domain.repository.BoardRepository
import javax.inject.Inject

class GetBoardsUseCase @Inject constructor(
    private val boardRepository: BoardRepository
) {
    operator fun invoke() = boardRepository.getAllBoards()
}