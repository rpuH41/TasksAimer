package com.liulkovich.tasksaimer.domain.usecase.user

import android.net.Uri
import com.liulkovich.tasksaimer.domain.repository.StorageRepository
import javax.inject.Inject

class UploadPhotoUseCase@Inject constructor(
    private val storageRepository: StorageRepository
) {
    suspend fun invoke(uri: Uri, userId: String) {
        storageRepository.uploadPhoto(uri, "users/$userId/profile.jpg")
    }
}