package sky.tavrov.suaclient.presentation.screen.login

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import sky.tavrov.suaclient.presentation.screen.common.StartActivityForResult
import sky.tavrov.suaclient.presentation.screen.common.signIn
import sky.tavrov.suaclient.util.logDebug

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginScreen(
    navController: NavController,
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    val singedInState by loginViewModel.signedInState
    val messageBarState by loginViewModel.messageBarState

    Scaffold(
        topBar = {
            LoginTopBar()
        },
        content = {
            LoginContent(
                signedInState = singedInState,
                messageBarState = messageBarState,
                onButtonClicked = {
                    loginViewModel.saveSignedInState(signedIn = true)
                }
            )
        }
    )

    val activity = LocalContext.current as Activity

    StartActivityForResult(
        key = singedInState,
        onResultReceive = { token ->
            logDebug("LoginScreen", token)
        },
        onDialogDismissed = {
            loginViewModel.saveSignedInState(false)
        }
    ) { activityLauncher ->
        if (singedInState) {
            signIn(
                activity = activity,
                launchActivityResult = {
                    activityLauncher.launch(it)
                },
                accountNotFound = {
                    loginViewModel.apply {
                        saveSignedInState(false)
                        updateMessageBarState()
                    }
                }
            )
        }
    }
}