package com.liulkovich.tasksaimer.presentation.components

import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.liulkovich.tasksaimer.presentation.navigation.Screen

@Composable
fun NavigationTasksAimerBottomBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute?.let { route ->
        !listOf(
            Screen.Welcome.route,
            Screen.SignIn.route,
            Screen.SignUp.route
        ).contains(route)
    } ?: false

    if (!showBottomBar) return

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
    ) {
        val items = listOf(
            Triple(Screen.Boards, Icons.Filled.Dashboard, Icons.Outlined.Dashboard),
            Triple(Screen.Notifications, Icons.Filled.Notifications, Icons.Outlined.Notifications),
            Triple(Screen.Profile, Icons.Filled.Person, Icons.Outlined.Person)
        )

        items.forEach { (screen, filledIcon, outlinedIcon) ->
                val isSelected = when (screen) {
                Screen.Boards -> currentRoute == Screen.Boards.route || currentRoute?.startsWith("tasks/") == true || currentRoute?.startsWith("create_task/") == true || currentRoute?.startsWith("taskDetails/") == true || currentRoute?.startsWith("create_board") == true
                Screen.Profile -> currentRoute == Screen.Profile.route
                Screen.Notifications -> currentRoute == Screen.Notifications.route
                else -> false
            }

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (isSelected) filledIcon else outlinedIcon,
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