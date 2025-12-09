@file:OptIn(ExperimentalMaterial3Api::class)

package com.liulkovich.tasksaimer.presentation.components

import android.net.Uri
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.liulkovich.tasksaimer.presentation.navigation.Screen
import com.liulkovich.tasksaimer.presentation.screen.createtask.CreateTaskViewModel

@Composable
fun DynamicTopBar(
    navController: NavHostController
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    when (currentRoute?.substringBefore("/{")) {

        Screen.Boards.route -> {
            CenterAlignedTopAppBar(
//                modifier = Modifier
//                    .border(width = 1.dp, color = MaterialTheme.colorScheme.outline, shape = RoundedCornerShape(16.dp)),
                title = {
                    Text(
                        text = "Boards",
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,


                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,

                ),
                modifier = Modifier
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(16.dp),
                        clip = false
                    )
                    .clip(RoundedCornerShape(16.dp))
            )
        }

        "tasks" -> {
            val boardTitle = currentBackStackEntry!!.arguments
                ?.getString("boardTitle")
                ?.let { Uri.decode(it) } ?: "Tasks"

            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                ),
                modifier = Modifier
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(16.dp),
                        clip = false
                    )
                    .clip(RoundedCornerShape(16.dp)),
               // modifier = Modifier.border(width = 1.dp, color = MaterialTheme.colorScheme.outline, shape = RoundedCornerShape(16.dp)),
                title = {
                    Text(
                        text = boardTitle,

                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                /*actions = {
                    IconButton(onClick = { /* Поиск */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }*/
            )
        }

        Screen.CreateTask.route.substringBefore("/{"), "create_task" -> {
            val viewModel: CreateTaskViewModel = hiltViewModel(currentBackStackEntry!!)

            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                ),
                modifier = Modifier
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(16.dp),
                        clip = false
                    )
                    .clip(RoundedCornerShape(16.dp)),
                //modifier = Modifier.border(width = 1.dp, color = MaterialTheme.colorScheme.outline, shape = RoundedCornerShape(16.dp)),
                title = {
                    Text(
                        "Create Task",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                },

            )
        }

        Screen.CreateBoard.route -> {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                ),
                modifier = Modifier
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(16.dp),
                        clip = false
                    )
                    .clip(RoundedCornerShape(16.dp)),
                //modifier = Modifier.border(width = 1.dp, color = MaterialTheme.colorScheme.outline, shape = RoundedCornerShape(16.dp)),
                title = {
                        Text(
                            text = "New Board",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )

                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                },
                actions = { Spacer(modifier = Modifier.width(48.dp)) },
                //colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }

        Screen.TaskDetail.route -> {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                ),
                modifier = Modifier
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(16.dp),
                        clip = false
                    )
                    .clip(RoundedCornerShape(16.dp)),
                //modifier = Modifier.border(width = 1.dp, color = MaterialTheme.colorScheme.outline, shape = RoundedCornerShape(16.dp)),
                title = {
                        Text(
                            text = "Development",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,

                        )

                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Close")
                    }
                }
            )
        }

        Screen.Profile.route -> {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                ),
                modifier = Modifier
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(16.dp),
                        clip = false
                    )
                    .clip(RoundedCornerShape(16.dp)),
                title = {
                    Text(
                        text = "Profile",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Close")
                   }
                },

            /*actions = {
                IconButton(onClick = { /* Поиск */ }) {
                    Icon(Icons.Default.Settings, contentDescription = "Search")
                }
            }*/

            )
        }

        else -> {
            if (currentRoute !in listOf("welcome", "sign_in", "sign_up")) {
                TopAppBar(title = { Text("TasksAimer") })
            }
        }
    }
}