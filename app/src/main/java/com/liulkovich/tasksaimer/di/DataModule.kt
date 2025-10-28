package com.liulkovich.tasksaimer.di

import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.liulkovich.tasksaimer.data.repository.BoardRepositoryImp
import com.liulkovich.tasksaimer.data.repository.TaskRepositoryImp
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

    @Singleton
    @Binds
    fun bindTasksRepository(
        impl: TaskRepositoryImp
    ): TaskRepository

    @Singleton
    @Binds
    fun bindBoardRepository(
        impl: BoardRepositoryImp
    ): BoardRepository



    companion object{
        @Provides
        @Singleton
        fun provideFirebaseFirestore(): FirebaseFirestore {
            return Firebase.firestore
        }
    }


}