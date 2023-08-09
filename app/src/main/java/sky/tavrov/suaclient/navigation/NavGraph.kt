package sky.tavrov.suaclient.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun SetupNavController(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route,
    ) {
        composable(route = Screen.Login.route) {

        }
        composable(route = Screen.Profile.route) {

        }
    }
}