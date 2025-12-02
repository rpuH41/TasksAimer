package com.liulkovich.tasksaimer.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.liulkovich.tasksaimer.presentation.navigation.Screen
@Composable
fun NavigationTasksAimerBottomBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route?.substringBefore("/{")

    if (currentRoute !in setOf("welcome", "sign_in", "sign_up")) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            listOf(Screen.Boards, Screen.Notifications, Screen.Profile).forEach { screen ->
                NavigationBarItem(
                    selected = currentRoute == screen.route,
                    onClick = {
                        navController.navigate(screen.route) {

                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        val (filled, outlined) = when (screen) {
                            Screen.Boards -> Icons.Filled.Dashboard to Icons.Outlined.Dashboard
                            Screen.Notifications -> Icons.Filled.Notifications to Icons.Outlined.Notifications
                            Screen.Profile -> Icons.Filled.Person to Icons.Outlined.Person
                            else -> Icons.Filled.Dashboard to Icons.Outlined.Dashboard
                        }
                        Icon(
                            imageVector = if (currentRoute == screen.route) filled else outlined,
                            contentDescription = null
                        )
                    },
                    label = {
                        Text(
                            text = when (screen) {
                                Screen.Boards -> "Boards"
                                Screen.Notifications -> "Notifications"
                                Screen.Profile -> "Profile"
                                else -> ""
                            }
                        )
                    },
                    alwaysShowLabel = false
                )
            }
        }
    }
}