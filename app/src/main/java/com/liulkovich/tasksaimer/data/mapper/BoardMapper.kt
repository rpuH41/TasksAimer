package com.liulkovich.tasksaimer.data.mapper

import com.liulkovich.tasksaimer.data.remote.BoardDTO
import com.liulkovich.tasksaimer.domain.entiity.Board

fun BoardDTO.toDomain(): Board {
    return Board(
        id = this.id ?: throw IllegalStateException("Board ID is required, cannot be null."),
        title = this.title ?: throw IllegalStateException("Board Title is required, cannot be null."),
        description = this.description,
        imageUrl = this.imageUrl,
        // tasksCount всегда имеет дефолт 0 в DTO, поэтому безопасно
        tasksCount = this.tasksCount,
        dueDate = this.dueDate,
        ownerId = this.ownerId ?: throw IllegalStateException("Board Owner ID is required, cannot be null."),
        // members имеет дефолт emptyList() в DTO, поэтому безопасно
        members = this.members,
        // Если createdAt null, используем текущее время (безопасный дефолт)
        createdAt = this.createdAt ?: System.currentTimeMillis()
    )
}

/**
 * Преобразует Board (Domain Layer) в BoardDTO (Data Layer).
 * Используется при записи данных в Firestore.
 */
fun Board.toDto(): BoardDTO {
    return BoardDTO(
        // Для новой доски id будет пустой строкой.
        // Мы передаем null, чтобы Firestore сгенерировал новый ID.
        id = if (this.id.isBlank()) null else this.id,
        title = this.title,
        description = this.description,
        imageUrl = this.imageUrl,
        tasksCount = this.tasksCount,
        dueDate = this.dueDate,
        ownerId = this.ownerId,
        members = this.members,
        createdAt = this.createdAt
    )
}