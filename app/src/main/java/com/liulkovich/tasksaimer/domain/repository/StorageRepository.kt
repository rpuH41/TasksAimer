package com.liulkovich.tasksaimer.domain.repository

import android.net.Uri
import java.net.URI

interface StorageRepository {

    suspend fun uploadPhoto(uri: Uri, path: String): Result<String>

    suspend fun deletePhoto(path: String): Result<Unit>
}