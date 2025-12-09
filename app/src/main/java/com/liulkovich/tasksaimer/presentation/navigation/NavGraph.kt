package com.liulkovich.tasksaimer.presentation.navigation

import android.net.Uri
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.liulkovich.tasksaimer.presentation.screen.auth.SignInScreen
import com.liulkovich.tasksaimer.presentation.screen.auth.SignUpScreen
import com.liulkovich.tasksaimer.presentation.screen.auth.WelcomeScreen
import com.liulkovich.tasksaimer.presentation.screen.boards.BoardsScreen
import com.liulkovich.tasksaimer.presentation.screen.createboard.CreateBoardScreen
import com.liulkovich.tasksaimer.presentation.screen.createtask.CreateTaskScreen
import com.liulkovich.tasksaimer.presentation.screen.profile.ProfileScreen
import com.liulkovich.tasksaimer.presentation.screen.taskdetails.TaskDetailsScreen
import com.liulkovich.tasksaimer.presentation.screen.tasks.TasksScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    paddingValues: PaddingValues,
    startDestination: String

) {

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier.padding(paddingValues)

    ) {
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onSignInClick = { navController.navigate(Screen.SignIn.route) },
                onSignUpClick = { navController.navigate(Screen.SignUp.route) }
            )
        }

        composable(Screen.SignIn.route) {
            SignInScreen(
                onSignUpClick = { navController.navigate(Screen.SignUp.route) },
                onBoardsClick = {
                    navController.navigate(Screen.Boards.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(onSignInClick = { navController.navigate(Screen.SignIn.route) })
        }

        composable(Screen.Boards.route) {
            BoardsScreen(
                onCreateBoardClick = { navController.navigate(Screen.CreateBoard.route) },
                onOpenBoardClick = { boardId, boardTitle ->
                    val route = Screen.Tasks.createRoute(boardId, boardTitle)
                    navController.navigate(route)
                }
            )
        }

        composable(Screen.Notifications.route) {
            //NotificationsScreen()
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onOpenBoardClick = { boardId, boardTitle ->
                    val route = Screen.Tasks.createRoute(boardId, boardTitle)
                    navController.navigate(route)
                },
                onLogout = {

                    navController.navigate("welcome") {
                        popUpTo(0) { inclusive = true }
                    }
                    //navController.navigate(Screen.Welcome.route) {
                    //    popUpTo(Screen.Boards.route) { inclusive = true }
                   // }
                }
            )
        }

        composable(Screen.CreateBoard.route) {
            //CreateBoardScreen(onFinished = { navController.popBackStack() })
            CreateBoardScreen(
                onBackClick = { navController.popBackStack() },
                onSaveClick = { navController.popBackStack() }
            )
        }

        composable(
            route = "tasks/{boardId}/{boardTitle}",
            arguments = listOf(
                navArgument("boardId") { type = NavType.StringType },
                navArgument("boardTitle") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val boardId = backStackEntry.arguments?.getString("boardId") ?: return@composable
            val boardTitle = backStackEntry.arguments?.getString("boardTitle") ?: ""

            TasksScreen(
                boardId = boardId,
                boardTitle = boardTitle,
                onCreateTaskClick = {
                    navController.navigate("create_task/$boardId")
                },
                onBackClick = { navController.popBackStack() },
                onOpenTaskDetailClick = { taskId, taskTitle ->
                   // navController.navigate("taskDetails/$taskId?taskTitle=${Uri.encode(taskTitle)}")
                    val route = Screen.TaskDetail.createRoute(taskId, taskTitle)
                    navController.navigate(route)
                }

            )
        }
        composable(
            route = "create_task/{boardId}",
            arguments = listOf(navArgument("boardId") { type = NavType.StringType })
        ) { backStackEntry ->
            val boardId = backStackEntry.arguments!!.getString("boardId")!!

            CreateTaskScreen(
                boardId = boardId,
                navController = navController
            )
        }
        composable(
            route = "taskDetails/{taskId}?taskTitle={taskTitle}",
            arguments = listOf(
                navArgument("taskId") { type = NavType.StringType },
                navArgument("taskTitle") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                }
            )
        ) { backStackEntry ->

            val taskId = backStackEntry.arguments?.getString("taskId")!!
            val taskTitle = backStackEntry.arguments?.getString("taskTitle") ?: ""

            TaskDetailsScreen(
                //navController = navController,
                taskId = taskId,
                taskTitle = taskTitle,
                onBack = { navController.popBackStack() }
            )
        }

    }
}

sealed class Screen(val route: String) {

    data object Welcome : Screen("welcome")

    data object SignIn : Screen("sign_in")

    data object SignUp : Screen("sign_up")

    data object Boards : Screen("boards")

    data object Notifications : Screen("notifications")

    data object Profile : Screen("profile")

    data object CreateBoard : Screen("create_board")

    data object Tasks : Screen("tasks/{boardId}/{boardTitle}") {
        fun createRoute(boardId: String, title: String) = "tasks/$boardId/${Uri.encode(title)}"
    }

    data object CreateTask : Screen("create_task/{boardId}") {
        fun createRoute(boardId: String) = "create_task/$boardId"
    }

    data object TaskDetail : Screen("taskDetails") {

        fun createRoute(taskId: String, taskTitle: String): String {
            val encoded = Uri.encode(taskTitle)
            return "taskDetails/$taskId?taskTitle=$encoded"
        }
    }
}