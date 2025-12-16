package com.liulkovich.tasksaimer.domain.repository

import com.liulkovich.tasksaimer.domain.entity.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    fun getTasksForBoard(boardId: String): Flow<List<Task>> //Отображает весь список задач

    fun getTaskForId(taskId: String): Flow<Task?> //Получаем одну задачу

    suspend fun addTask(task: Task) //Добавление задачи

    fun sortedStatus(boardId: String, status: String): Flow<List<Task>> //сортировать по статусу

    suspend fun editTask(task: Task) //изминение задачи

    suspend fun deleteTask(taskId: String) //удаление задачи

    suspend fun deleteAllTasksByBoardId(boardId: String) //удаляет все задачи при удаление доски

    fun searchTaskByTitle(boardId: String, title: String): Flow<List<Task>> //поиск по зоголовку

    suspend fun updateTaskAssigneesAndBoardMembers(taskId: String?, boardId: String, assigneeIds: List<String>)
}