package com.liulkovich.tasksaimer.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.liulkovich.tasksaimer.presentation.screen.auth.SignInScreen
import com.liulkovich.tasksaimer.presentation.screen.auth.SignUpScreen
import com.liulkovich.tasksaimer.presentation.screen.auth.WelcomeScreen

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
            )
        }
        composable(Screen.SignUp.route){
            SignUpScreen(
                onSignInClick = {
                    navController.navigate(Screen.SignIn.route)
                }
            )
        }
    }
}

sealed class Screen(val route: String) {

    data object Welcome: Screen("welcome")

    data object SignIn: Screen("sign_in")

    data object SignUp: Screen("sign_up")

}