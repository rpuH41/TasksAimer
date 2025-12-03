package com.liulkovich.tasksaimer.data.mapper

import com.liulkovich.tasksaimer.data.remote.TaskDTO
import com.liulkovich.tasksaimer.domain.entiity.Priority
import com.liulkovich.tasksaimer.domain.entiity.Status
import com.liulkovich.tasksaimer.domain.entiity.Task

// --- 1. Преобразование DTO в DOMAIN (Получение из Firestore) ---
fun TaskDTO.toDomain(): Task {
    // Проверка обязательных полей. Если они null, это ошибка данных в БД.
    val domainId = id ?: throw IllegalStateException("Task ID cannot be null when mapping to Domain.")
    val domainBoardId = boardId ?: throw IllegalStateException("Task BoardID cannot be null when mapping to Domain.")
    val domainOwnerId = ownerId
        ?: com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
        ?: ""


    // Title обязателен в домене, но может прийти null. Устанавливаем безопасное значение.
    val domainTitle = title ?: "Untitled Task"

    // Преобразование String в Enum. Используем try-catch для обработки ошибок.
    val domainPriority = try {
        // Если priority == null, берем строковое имя дефолтного Enum, иначе - строку из DTO.
        Priority.valueOf(priority ?: Priority.MEDIUM.name)
    } catch (e: IllegalArgumentException) {
        // Если строка невалидна, возвращаем безопасное дефолтное значение.
        Priority.MEDIUM
    }

    val domainStatus = try {
        Status.valueOf(status ?: Status.TODO.name)
    } catch (e: IllegalArgumentException) {
        Status.TODO
    }


    return Task(
        id = domainId,
        boardId = domainBoardId,
        title = domainTitle,
        description = description,
        dueDate = dueDate,
        dueTime = dueTime,
        priority = domainPriority,
        status = domainStatus,
        assignedTo = assignedTo,
        // Если createdAt null, используем текущее время (для гарантии).
        createdAt = createdAt ?: System.currentTimeMillis(),
        ownerId = domainOwnerId,
        updatedAt = updatedAt ?: System.currentTimeMillis()
    )
}

// --- 2. Преобразование DOMAIN в DTO (Отправка в Firestore) ---
fun Task.toDto(): TaskDTO {
    return TaskDTO(
        // Если ID задачи - пустая строка, отправляем null, чтобы Firestore сгенерировал новый.
        id = if (this.id.isNullOrBlank()) null else this.id,
        boardId = this.boardId,
        title = this.title,
        description = this.description,
        dueDate = this.dueDate,
        dueTime = this.dueTime,
        // Преобразование Enum в String для записи в Firestore.
        priority = this.priority.name,
        status = this.status.name,
        assignedTo = this.assignedTo,
        createdAt = this.createdAt
    )
}
