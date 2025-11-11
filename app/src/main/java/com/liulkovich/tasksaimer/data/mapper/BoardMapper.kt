package com.liulkovich.tasksaimer.data.mapper

import com.liulkovich.tasksaimer.data.remote.BoardDTO
import com.liulkovich.tasksaimer.domain.entiity.Board

fun BoardDTO.toDomain(documentId: String): Board {
    return Board(
        id = documentId,
        title = this.title ?: "",  // ← ПУСТАЯ СТРОКА, НЕ throw!
        description = this.description,
        tasksCount = this.tasksCount ?: 0,
        dueDate = this.dueDate,
        ownerId = this.ownerId ?: "",  // ← ПУСТАЯ СТРОКА!
        members = this.members ?: emptyList(),  // ← НЕ null!
        createdAt = this.createdAt ?: System.currentTimeMillis(),
        updatedAt = this.updatedAt ?: System.currentTimeMillis()
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
        //id = if (this.id.isNullOrBlank()) null else this.id,
        title = this.title,
        description = this.description,
        //imageUrl = this.imageUrl,
        tasksCount = this.tasksCount,
        dueDate = this.dueDate,
        ownerId = this.ownerId,
        members = this.members,
        createdAt = this.createdAt
    )
}