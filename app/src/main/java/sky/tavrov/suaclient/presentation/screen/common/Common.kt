package sky.tavrov.suaclient.presentation.screen.common

import android.app.Activity
import android.app.Activity.RESULT_OK
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes.CANCELED
import com.google.android.gms.common.api.CommonStatusCodes.NETWORK_ERROR
import sky.tavrov.suaclient.util.Constants.CLIENT_ID
import sky.tavrov.suaclient.util.logDebug

@Composable
fun StartActivityForResult(
    key: Any,
    onResultReceive: (String) -> Unit,
    onDialogDismissed: () -> Unit,
    launcher: (ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>) -> Unit
) {
    val activity = LocalContext.current as Activity
    val activityLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) {
        try {
            if (it.resultCode == RESULT_OK) {
                val oneTapClient = Identity.getSignInClient(activity)
                val credentials = oneTapClient.getSignInCredentialFromIntent(it.data)
                val idToken = credentials.googleIdToken
                if (idToken != null) {
                    onResultReceive(idToken)
                } else {
                    logDebug(
                        "StartActivityForResult",
                        "BLACK SCRIM CLICKED, DIALOG CLOSED.",
                        onDialogDismissed
                    )
                }
            }
        } catch (e: ApiException) {
            when (e.statusCode) {
                CANCELED -> {
                    logDebug(
                        "StartActivityForResult",
                        "ONE-TAP DIALOG CANCELLED.",
                        onDialogDismissed
                    )
                }

                NETWORK_ERROR -> {
                    logDebug(
                        "StartActivityForResult",
                        "ONE-TAP NETWORK ERROR.",
                        onDialogDismissed
                    )
                }

                else -> {
                    logDebug(
                        "StartActivityForResult",
                        "${e.message}",
                        onDialogDismissed
                    )
                }
            }
        }
    }

    LaunchedEffect(key1 = key) {
        launcher(activityLauncher)
    }
}

fun signIn(
    activity: Activity,
    launchActivityResult: (IntentSenderRequest) -> Unit,
    accountNotFound: () -> Unit
) {
    val oneTapClient = Identity.getSignInClient(activity)
    val signInRequest = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId(CLIENT_ID)
                .setFilterByAuthorizedAccounts(true)
                .build()
        )
        .setAutoSelectEnabled(true)
        .build()

    oneTapClient.beginSignIn(signInRequest)
        .addOnSuccessListener {
            try {
                launchActivityResult(
                    IntentSenderRequest.Builder(it.pendingIntent.intentSender).build()
                )
            } catch (e: Exception) {
                logDebug("SignIn", "Could not start One Tap UI: ${e.message}")
            }
        }
        .addOnFailureListener {
            logDebug("SignIn", "Signing Up...")

            signUp(activity, launchActivityResult, accountNotFound)
        }
}

fun signUp(
    activity: Activity,
    launchActivityResult: (IntentSenderRequest) -> Unit,
    accountNotFound: () -> Unit
) {
    val oneTapClient = Identity.getSignInClient(activity)
    val signInRequest = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId(CLIENT_ID)
                .setFilterByAuthorizedAccounts(false)
                .build()
        )
        .build()

    oneTapClient.beginSignIn(signInRequest)
        .addOnSuccessListener {
            try {
                launchActivityResult(
                    IntentSenderRequest.Builder(it.pendingIntent.intentSender).build()
                )
            } catch (e: Exception) {
                logDebug("SignIn", "Could not start One Tap UI: ${e.message}")
            }
        }
        .addOnFailureListener {
            logDebug("SignIn", "Signing Up...")

            accountNotFound()
        }
}