package com.liulkovich.tasksaimer.domain.entity

data class Task(
    val id: String? = null,
    val boardId: String,
    val title: String,
    val description: String? = null,
    val dueDate: String? = null,
    val dueTime: String? = null,
    val priority: Priority = Priority.MEDIUM,
    val status: Status = Status.TODO,
    val assignedTo: List<String> = emptyList(),
    val ownerId: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
