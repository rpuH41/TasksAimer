package com.liulkovich.tasksaimer.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.liulkovich.tasksaimer.presentation.screen.auth.SignUpScreen
import com.liulkovich.tasksaimer.presentation.ui.theme.TasksAimerTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TasksAimerTheme{
                Scaffold(modifier = Modifier.fillMaxWidth()) { innerPadding ->
                    SignUpScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}