package com.liulkovich.tasksaimer.domain.entiity


data class Board(
    val id: String? = null,
    val title: String,
    val description: String? = null,
    val imageUrl: String? = null,
    val tasksCount: Int = 0,
    val dueDate: String? = null,
    val ownerId: String,
    val members: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
