package com.liulkovich.tasksaimer.presentation.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.liulkovich.tasksaimer.presentation.screen.auth.SignInScreen
import com.liulkovich.tasksaimer.presentation.screen.auth.SignUpScreen
import com.liulkovich.tasksaimer.presentation.screen.auth.WelcomeScreen
import com.liulkovich.tasksaimer.presentation.screen.boards.BoardsScreen
import com.liulkovich.tasksaimer.presentation.screen.createboard.CreateBoardScreen
import com.liulkovich.tasksaimer.presentation.screen.createtask.CreateTaskScreen
import com.liulkovich.tasksaimer.presentation.screen.tasks.TasksScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route
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

        composable(Screen.CreateBoard.route) {
            CreateBoardScreen(onFinished = { navController.popBackStack() })
        }

        // ← ПРАВИЛЬНЫЙ маршрут с boardId и boardTitle
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
                onBackTaskClick = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = "create_task/{boardId}",
            arguments = listOf(navArgument("boardId") { type = NavType.StringType })
        ) {
            CreateTaskScreen()
        }
    }
}

sealed class Screen(val route: String) {
    data object Welcome : Screen("welcome")
    data object SignIn : Screen("sign_in")
    data object SignUp : Screen("sign_up")
    data object Boards : Screen("boards")
    data object CreateBoard : Screen("create_board")

    data object Tasks : Screen("tasks/{boardId}/{boardTitle}") {
        fun createRoute(boardId: String, title: String) = "tasks/$boardId/${Uri.encode(title)}"
    }

    data object CreateTask : Screen("create_task/{boardId}") {
        fun createRoute(boardId: String) = "create_task/$boardId"
    }
}