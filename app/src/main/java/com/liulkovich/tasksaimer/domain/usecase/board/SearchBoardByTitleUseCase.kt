package com.liulkovich.tasksaimer.domain.usecase.board

import com.liulkovich.tasksaimer.domain.entity.Board
import com.liulkovich.tasksaimer.domain.repository.BoardRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchBoardByTitleUseCase @Inject constructor(
    private val boardRepository: BoardRepository
) {
    operator fun invoke(title: String, userId: String): Flow<List<Board>> {
        return boardRepository.searchBoardByTitle(title, userId)
    }
}