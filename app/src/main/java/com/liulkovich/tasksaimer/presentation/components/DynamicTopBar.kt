@file:OptIn(ExperimentalMaterial3Api::class)

package com.liulkovich.tasksaimer.presentation.components

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Поиск */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )
        }

        Screen.CreateTask.route.substringBefore("/{"), "create_task" -> {
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
                        Icon(Icons.Default.Close, contentDescription = "Close")
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
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                },
                actions = { Spacer(modifier = Modifier.width(48.dp)) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }

        Screen.TaskDetail.route -> {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Development",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Close")
                    }
                }
            )
        }

        else -> {
            if (currentRoute !in listOf("welcome", "sign_in", "sign_up")) {
                TopAppBar(title = { Text("TasksAimer") })
            }
        }
    }
}