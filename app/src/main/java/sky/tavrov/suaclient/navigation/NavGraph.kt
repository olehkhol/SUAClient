package sky.tavrov.suaclient.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import sky.tavrov.suaclient.presentation.screen.login.LoginScreen
import sky.tavrov.suaclient.presentation.screen.profile.ProfileScreen

@Composable
fun SetupNavController(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route,
    ) {
        composable(route = Screen.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(route = Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }
    }
}