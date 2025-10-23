package com.liulkovich.tasksaimer.domain.repository

import com.liulkovich.tasksaimer.domain.entiity.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    fun getTasksForBoard(boardId: String): Flow<List<Task>> //Отображает весь список задач

    suspend fun addTask(task: Task) //Добавление задачи

    fun sortedStatus(boardId: String, status: String): Flow<List<Task>> //сортировать по статусу

    suspend fun editTask(task: Task) //изминение задачи

    suspend fun deleteTask(taskId: String) //удаление задачи

    suspend fun deleteAllTasksByBoardId(boardId: String) //удаляет все задачи при удаление доски

}