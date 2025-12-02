package com.liulkovich.tasksaimer.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.navigation.compose.rememberNavController
import com.liulkovich.tasksaimer.presentation.components.DynamicFab
import com.liulkovich.tasksaimer.presentation.components.DynamicTopBar
import com.liulkovich.tasksaimer.presentation.components.NavigationTasksAimerBottomBar
import com.liulkovich.tasksaimer.presentation.navigation.NavGraph
import com.liulkovich.tasksaimer.presentation.ui.theme.TasksAimerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            TasksAimerTheme {
                val navController = rememberNavController()
                Scaffold(
                    topBar = { DynamicTopBar(
                        navController
                    ) },
                    floatingActionButton = { DynamicFab(navController) },
                    bottomBar = { NavigationTasksAimerBottomBar(navController) },
                    containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
                    NavGraph(
                        navController = navController,
                        paddingValues = innerPadding
                    )
                }
            }
        }
    }
}