package sky.tavrov.suaclient.presentation.screen.profile

import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfileScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val apiResponse by profileViewModel.apiResponse
    val messageBarState by profileViewModel.messageBarState

    val user by profileViewModel.user

    Scaffold(
        topBar = {
            ProfileTopBar(
                onSave = profileViewModel::updateUserInfo,
                onDeleteAllConfirmed = {}
            )
        },
        content = {
            ProfileContent(
                apiResponse = apiResponse,
                messageBarState = messageBarState,
                user = user,
                onFirstNameChanged = profileViewModel::updateFirstName,
                onLastNameChanged = profileViewModel::updateLastName,
                onSignOutClicked = {}
            )
        }
    )
}