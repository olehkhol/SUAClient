package sky.tavrov.suaclient.presentation.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import sky.tavrov.suaclient.R
import sky.tavrov.suaclient.component.GoogleButton
import sky.tavrov.suaclient.component.MessageBar
import sky.tavrov.suaclient.domain.model.MessageBarState

@Composable
fun LoginContent(
    signedInState: Boolean,
    messageBarState: MessageBarState,
    onButtonClicked: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.weight(1f)) {
            MessageBar(state = messageBarState)
        }
        Column(
            modifier = Modifier
                .weight(9f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CentralContent(
                signedInState = signedInState,
                onButtonClicked = onButtonClicked
            )
        }
    }
}

@Composable
fun CentralContent(
    signedInState: Boolean,
    onButtonClicked: () -> Unit
) {
    Image(
        modifier = Modifier
            .padding(bottom = 20.dp)
            .size(120.dp),
        painter = painterResource(id = R.drawable.ic_google_logo),
        contentDescription = "Google Logo"
    )
    Text(
        text = stringResource(id = R.string.sign_in_title),
        fontWeight = FontWeight.Bold,
        fontSize = MaterialTheme.typography.headlineSmall.fontSize
    )
    Text(
        modifier = Modifier
            .alpha(MaterialTheme.colorScheme.onSurfaceVariant.alpha)
            .padding(bottom = 40.dp, top = 4.dp),
        text = stringResource(id = R.string.sign_in_subtitle),
        fontSize = MaterialTheme.typography.titleMedium.fontSize,
        textAlign = TextAlign.Center
    )
    GoogleButton(
        loadingState = signedInState,
        onClick = onButtonClicked
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginContentPreview() {
    LoginContent(signedInState = false, messageBarState = MessageBarState(), onButtonClicked = {})
}