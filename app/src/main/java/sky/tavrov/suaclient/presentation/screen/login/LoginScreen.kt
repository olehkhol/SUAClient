package sky.tavrov.suaclient.presentation.screen.login

import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import sky.tavrov.suaclient.domain.model.MessageBarState

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginScreen(navController: NavController) {

    Scaffold(
        topBar = {
            LoginTopBar()
        },
        content = {
            LoginContent(
                signedInState = true,
                messageBarState = MessageBarState(),
                onButtonClicked = {}
            )
        }
    )
}