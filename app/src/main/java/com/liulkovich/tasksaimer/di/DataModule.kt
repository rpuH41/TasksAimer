package com.liulkovich.tasksaimer.di

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.liulkovich.tasksaimer.data.repository.BoardRepositoryImpl
import com.liulkovich.tasksaimer.data.repository.AuthRepositoryImpl
import com.liulkovich.tasksaimer.data.repository.TaskRepositoryImpl
import com.liulkovich.tasksaimer.domain.repository.AuthRepository
import com.liulkovich.tasksaimer.domain.repository.BoardRepository
import com.liulkovich.tasksaimer.domain.repository.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Binds
    @Singleton
    fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Singleton
    @Binds
    fun bindTasksRepository(
        impl: TaskRepositoryImpl
    ): TaskRepository

    @Singleton
    @Binds
    fun bindBoardRepository(
        impl: BoardRepositoryImpl
    ): BoardRepository

    companion object{
        @Provides
        @Singleton
        fun provideFirebaseFirestore(): FirebaseFirestore {
            return Firebase.firestore
        }

        @Provides
        @Singleton
        fun provideFirebaseAuth(): FirebaseAuth {
            return FirebaseAuth.getInstance()
        }
    }
}