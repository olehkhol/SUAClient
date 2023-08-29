package sky.tavrov.suaclient.presentation.screen.profile

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
import com.google.android.gms.auth.api.identity.Identity
import retrofit2.HttpException
import sky.tavrov.suaclient.domain.model.ApiRequest
import sky.tavrov.suaclient.domain.model.ApiResponse
import sky.tavrov.suaclient.navigation.Screen
import sky.tavrov.suaclient.presentation.screen.common.StartActivityForResult
import sky.tavrov.suaclient.presentation.screen.common.signIn
import sky.tavrov.suaclient.util.RequestState

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfileScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val apiResponse by profileViewModel.apiResponse
    val clearSessionResponse by profileViewModel.clearSessionResponse
    val messageBarState by profileViewModel.messageBarState
    val user by profileViewModel.user

    Scaffold(
        topBar = {
            ProfileTopBar(
                onSave = profileViewModel::updateUserInfo,
                onDeleteAllConfirmed = profileViewModel::deleteUser
            )
        },
        content = {
            ProfileContent(
                apiResponse = apiResponse,
                messageBarState = messageBarState,
                user = user,
                onFirstNameChanged = profileViewModel::updateFirstName,
                onLastNameChanged = profileViewModel::updateLastName,
                onSignOutClicked = profileViewModel::clearSession,
                modifier = Modifier.padding(it)
            )
        }
    )

    val activity = LocalContext.current as Activity

    StartActivityForResult(
        key = apiResponse,
        onResultReceive = {
            profileViewModel.verifyTokenOnBackend(request = ApiRequest(it))
        },
        onDialogDismissed = {
            signOut(profileViewModel, navController)
        }
    ) { activityLauncher ->
        if (apiResponse is RequestState.Success) {
            val response = (apiResponse as RequestState.Success).data
            if (response.error is HttpException && response.error.code() == 401) {
                signIn(
                    activity = activity,
                    accountNotFound = {
                        signOut(profileViewModel, navController)
                    },
                    launchActivityResult = {
                        activityLauncher.launch(it)
                    }
                )
            }
        } else if (apiResponse is RequestState.Error) {
            signOut(profileViewModel, navController)
        }
    }

    LaunchedEffect(key1 = clearSessionResponse) {
        if (clearSessionResponse is RequestState.Success &&
            (clearSessionResponse as RequestState.Success<ApiResponse>).data.success
        ) {
            val oneTapClient = Identity.getSignInClient(context)
            oneTapClient.signOut()
            signOut(profileViewModel, navController)
        }
    }
}

private fun signOut(
    profileViewModel: ProfileViewModel,
    navController: NavController
) {
    profileViewModel.saveSignedInState(signedIn = false)
    navigateToLoginScreen(navController = navController)
}

fun navigateToLoginScreen(
    navController: NavController
) {
    navController.navigate(route = Screen.Login.route) {
        popUpTo(Screen.Profile.route) {
            inclusive = true
        }
    }
}