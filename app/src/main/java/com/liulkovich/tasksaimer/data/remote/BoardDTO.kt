package com.liulkovich.tasksaimer.data.remote

data class BoardDTO(

    val id: String? = null,
    val title: String? = null,
    val description: String? = null,
    val imageUrl: String? = null,
    val tasksCount: Int = 0,
    val dueDate: String? = null,
    val ownerId: String? = null,
    val members: List<String> = emptyList(),
    val createdAt: Long? = null,
    val updatedAt: Long? = null
)
