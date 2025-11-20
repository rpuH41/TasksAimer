package com.liulkovich.tasksaimer.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.liulkovich.tasksaimer.presentation.navigation.Screen

@Composable
fun DynamicFab(navController: NavHostController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    when (currentRoute?.substringBefore("/{")) {
        Screen.Boards.route -> {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.CreateBoard.route) },
                containerColor = MaterialTheme.colorScheme.onSecondary,
                contentColor = MaterialTheme.colorScheme.surface
            ) {
                Icon(Icons.Filled.Add, "Create board")
            }
        }
        "tasks" -> {
            val boardId = navController.currentBackStackEntry?.arguments?.getString("boardId")
            boardId?.let {
                FloatingActionButton(
                    onClick = { navController.navigate("create_task/$it") },
                    containerColor = MaterialTheme.colorScheme.onSecondary,
                    contentColor = MaterialTheme.colorScheme.surface
                ) {
                    Icon(Icons.Filled.Add, "Create task")
                }
            }
        }
    }
}