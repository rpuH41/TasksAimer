package com.liulkovich.tasksaimer.domain.usecase.board

import com.liulkovich.tasksaimer.domain.repository.BoardRepository
import com.liulkovich.tasksaimer.domain.repository.TaskRepository
import javax.inject.Inject

class DeleteBoardByIdUseCase @Inject constructor(
    private val boardRepository: BoardRepository,
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(boardId: String) {
        taskRepository.deleteAllTasksByBoardId(boardId)
        boardRepository.deleteBoardById(boardId)
    }
}