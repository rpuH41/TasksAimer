package com.liulkovich.tasksaimer.domain.entiity

import com.google.firebase.firestore.PropertyName


data class Board @JvmOverloads constructor(
    @PropertyName("id") val id: String = "",
    @PropertyName("title") val title: String = "",
    @PropertyName("description") val description: String? = null,
    @PropertyName("tasksCount") val tasksCount: Int = 0,
    @PropertyName("dueDate") val dueDate: String? = null,
    @PropertyName("ownerId") val ownerId: String = "",
    @PropertyName("members") val members: List<String> = emptyList(),
    @PropertyName("createdAt") val createdAt: Long = System.currentTimeMillis(),
    @PropertyName("updatedAt") val updatedAt: Long = System.currentTimeMillis()
)
