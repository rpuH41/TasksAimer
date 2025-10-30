package com.liulkovich.tasksaimer.domain.entiity

data class User(
    val id: String = "",
    val firstName: String?,
    val lastName: String? = null,
    val email: String,
    //val userPhotoUrl: String? = null
)
