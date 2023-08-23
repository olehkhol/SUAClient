package sky.tavrov.suaclient.presentation.screen.login

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import sky.tavrov.suaclient.domain.model.ApiRequest
import sky.tavrov.suaclient.domain.model.ApiResponse
import sky.tavrov.suaclient.navigation.Screen
import sky.tavrov.suaclient.presentation.screen.common.StartActivityForResult
import sky.tavrov.suaclient.presentation.screen.common.signIn
import sky.tavrov.suaclient.util.RequestState

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginScreen(
    navController: NavController,
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    val singedInState by loginViewModel.signedInState
    val messageBarState by loginViewModel.messageBarState
    val apiResponse by loginViewModel.apiResponse

    Scaffold(
        topBar = {
            LoginTopBar()
        },
        content = {
            LoginContent(
                signedInState = singedInState,
                messageBarState = messageBarState,
                modifier = Modifier.padding(it),
            ) {
                loginViewModel.saveSignedInState(signedIn = true)
            }
        }
    )

    val activity = LocalContext.current as Activity

    StartActivityForResult(
        key = singedInState,
        onResultReceive = {
            loginViewModel.verifyTokenOnBackend(ApiRequest(it))
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
                    loginViewModel.saveSignedInState(false)
                    loginViewModel.updateMessageBarState()
                }
            )
        }
    }

    LaunchedEffect(key1 = apiResponse) {
        when (apiResponse) {
            is RequestState.Success -> {
                val success = (apiResponse as RequestState.Success<ApiResponse>).data.success

                if (success) {
                    navigateToProfileScreen(navController)
                } else {
                    loginViewModel.saveSignedInState(false)
                }
            }

            is RequestState.Error -> loginViewModel.saveSignedInState(false)
            else -> {}
        }
    }
}

private fun navigateToProfileScreen(navController: NavController) {
    navController.navigate(route = Screen.Profile.route) {
        popUpTo(route = Screen.Login.route) {
            inclusive = true
        }
    }
}