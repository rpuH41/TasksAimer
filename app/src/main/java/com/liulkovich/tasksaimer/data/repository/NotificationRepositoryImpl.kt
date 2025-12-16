package com.liulkovich.tasksaimer.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.liulkovich.tasksaimer.domain.entity.Notification
import com.liulkovich.tasksaimer.domain.repository.NotificationRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.jvm.java

class NotificationRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : NotificationRepository {

    private fun notificationsCollection(userId: String) =
        firestore.collection("users").document(userId).collection("notifications")

    override fun getNotificationsForUser(userId: String): Flow<List<Notification>> = callbackFlow {
        val listener = notificationsCollection(userId)
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val notifications = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Notification::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(notifications)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun markAsRead(userId: String, notificationId: String) {
        notificationsCollection(userId)
            .document(notificationId)
            .update("isRead", true)
            .await()
    }

    override suspend fun clearAllNotifications(userId: String) {
        val snapshot = notificationsCollection(userId).get().await()
        for (doc in snapshot.documents) {
            doc.reference.delete().await()
        }
    }

    override suspend fun createNotification(userId: String, notification: Notification) {
        notificationsCollection(userId)
            .add(notification.copy(id = ""))  // id сгенерируется автоматически
            .await()
    }
}