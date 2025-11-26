package com.liulkovich.tasksaimer.presentation.screen.profile

import androidx.lifecycle.ViewModel
import com.liulkovich.tasksaimer.domain.usecase.auth.GetCurrentUserUseCase
import com.liulkovich.tasksaimer.domain.usecase.board.GetBoardsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getBoardsUseCase: GetBoardsUseCase,

): ViewModel(){

}