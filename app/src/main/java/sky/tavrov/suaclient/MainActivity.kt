package sky.tavrov.suaclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import sky.tavrov.suaclient.navigation.SetupNavController
import sky.tavrov.suaclient.ui.theme.SUAClientTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SUAClientTheme {
                val navController = rememberNavController()

                SetupNavController(navController = navController)
            }
        }
    }
}