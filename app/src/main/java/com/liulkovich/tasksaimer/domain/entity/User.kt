package com.liulkovich.tasksaimer.domain.entity

import com.google.firebase.firestore.PropertyName

data class User @JvmOverloads constructor(
    @PropertyName("id") val id: String = "",
    @PropertyName("firstName") val firstName: String? = null,
    @PropertyName("fcmToken") val fcmToken: String? = null,
    @PropertyName("lastName") val lastName: String? = null, //change on phone
    @PropertyName("email") val email: String = ""
)
