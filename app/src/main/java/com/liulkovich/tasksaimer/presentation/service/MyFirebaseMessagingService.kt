package com.liulkovich.tasksaimer.presentation.service

import com.google.firebase.messaging.RemoteMessage
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM_TOKEN", "Новый FCM токен: $token")

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .update("fcmToken", token)
                .addOnSuccessListener {
                    Log.d("FCM_TOKEN", "Токен успешно сохранён в Firestore")
                }
                .addOnFailureListener { e ->
                    Log.e("FCM_TOKEN", "Ошибка сохранения токена", e)
                }
        } else {
            Log.w("FCM_TOKEN", "Пользователь не авторизован — токен не сохранён")
        }
    }


    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("FCM_MESSAGE", "Получено сообщение в foreground: ${message.notification?.title}")

    }
}