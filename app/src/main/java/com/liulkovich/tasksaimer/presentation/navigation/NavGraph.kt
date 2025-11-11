package com.liulkovich.tasksaimer.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.liulkovich.tasksaimer.presentation.screen.auth.SignInScreen
import com.liulkovich.tasksaimer.presentation.screen.auth.SignUpScreen
import com.liulkovich.tasksaimer.presentation.screen.auth.WelcomeScreen
import com.liulkovich.tasksaimer.presentation.screen.boards.BoardsScreen
import com.liulkovich.tasksaimer.presentation.screen.createboard.CreateBoardScreen

@Composable
fun NavGraph(){
    val navController = rememberNavController()

    NavHost(
       navController = navController,
       startDestination = Screen.Welcome.route
    ){
        composable(Screen.Welcome.route){
            WelcomeScreen(
                onSignInClick = {
                    navController.navigate(Screen.SignIn.route)
                },
                onSignUpClick = {
                    navController.navigate(Screen.SignUp.route)
                }
            )
        }
        composable(Screen.SignIn.route){
            SignInScreen(
                onSignUpClick = {
                    navController.navigate(Screen.SignUp.route)
                },
                onBoardsClick = {
                    navController.navigate(Screen.Boards.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(Screen.SignUp.route){
            SignUpScreen(
                onSignInClick = {
                    navController.navigate(Screen.SignIn.route)
                }
            )
        }
        composable( Screen.Boards.route ) {
            BoardsScreen(
                onCreateBoardClick = {
                    navController.navigate(Screen.CreateBoard.route)
                }
            )
        }
        composable( Screen.CreateBoard.route ) {
            CreateBoardScreen(
                onFinished = {
                    navController.popBackStack()
                }
            )
        }
    }
}

sealed class Screen(val route: String) {

    data object Welcome: Screen("welcome")

    data object SignIn: Screen("sign_in")

    data object SignUp: Screen("sign_up")

    data object Boards: Screen("boards")

    data object CreateBoard: Screen("create_board")

}