@file:OptIn(ExperimentalMaterial3Api::class)

package com.liulkovich.tasksaimer.presentation.components

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.liulkovich.tasksaimer.presentation.navigation.Screen
import com.liulkovich.tasksaimer.presentation.screen.createtask.CreateTaskCommand
import com.liulkovich.tasksaimer.presentation.screen.createtask.CreateTaskViewModel

@Composable
fun DynamicTopBar(
    navController: NavHostController
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    when (currentRoute?.substringBefore("/{")) {

        Screen.Boards.route -> {
            TopAppBar(
                title = {
                    Text(
                        text = "Boards",
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }

        "tasks" -> {
            val boardTitle = currentBackStackEntry!!.arguments
                ?.getString("boardTitle")
                ?.let { Uri.decode(it) } ?: "Tasks"

            TopAppBar(
                title = {
                    Text(
                        text = boardTitle,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Поиск */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Поиск")
                    }
                }
            )
        }

        // Экран создания задачи — магия здесь
        Screen.CreateTask.route.substringBefore("/{"), "create_task" -> {
            // Автоматически получаем ViewModel текущего экрана
            val viewModel: CreateTaskViewModel = hiltViewModel(currentBackStackEntry!!)

            TopAppBar(
                title = {
                    Text(
                        "Create Task",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.Close, contentDescription = "Закрыть")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            viewModel.processCommand(CreateTaskCommand.SaveTask)
                            navController.popBackStack()
                        }
                    ) {
                        Text("Save", color = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        }

        Screen.CreateBoard.route -> {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "New Board",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.Close, contentDescription = "Закрыть")
                    }
                },
                actions = { Spacer(modifier = Modifier.width(48.dp)) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }

        else -> {
            if (currentRoute !in listOf("welcome", "sign_in", "sign_up")) {
                TopAppBar(title = { Text("TasksAimer") })
            }
        }
    }
}