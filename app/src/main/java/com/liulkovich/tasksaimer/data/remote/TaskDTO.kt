package com.liulkovich.tasksaimer.data.remote

data class TaskDTO (
    val id: String? = null,
    val boardId: String? = null,
    val title: String? = null,
    val titleLowercase: String = "",
    val description: String? = null,
    val dueDate: String? = null,
    val dueTime: String? = null,
    val priority: String? = null,
    val status: String? = null,
    val assignedTo: List<String> = emptyList(),
    val createdAt: Long? = null,
    val updatedAt: Long? = null,
    val ownerId: String? = null
)