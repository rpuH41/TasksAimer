package com.liulkovich.tasksaimer.domain.repository

import com.liulkovich.tasksaimer.domain.entiity.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    fun getTasksForBoard(boardId: Int): Flow<List<Task>> //Отображает весь список задач

    suspend fun addTask(task: Task) //Добавление задачи

    fun sortedStatus(boardId: Int, status: String): Flow<List<Task>> //сортировать по статусу

    suspend fun editTask(task: Task) //изминение задачи

    suspend fun deleteTask(taskId: Int) //удаление задачи

    suspend fun deleteAllTasksByBoardId(boardId: Int) //удаляет все задачи при удаление доски

}