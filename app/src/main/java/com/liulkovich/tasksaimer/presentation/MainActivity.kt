package com.liulkovich.tasksaimer.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.liulkovich.tasksaimer.domain.entiity.Board
import com.liulkovich.tasksaimer.presentation.ui.theme.TasksAimerTheme

class MainActivity : ComponentActivity() {
   // val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
//            val test = Board(
//                "1",
//                "test",
//                "testing",
//                null,
//                1,
//                "2",
//                "2",
//
//            )
//            db.collection("Boards").document("test").set(test)
        }
    }
}