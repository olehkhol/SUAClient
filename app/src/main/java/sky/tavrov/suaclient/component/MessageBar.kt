package sky.tavrov.suaclient.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import sky.tavrov.suaclient.domain.model.MessageBarState
import sky.tavrov.suaclient.ui.theme.ErrorRed
import sky.tavrov.suaclient.ui.theme.InfoGreen
import java.net.ConnectException
import java.net.SocketTimeoutException

@Composable
fun MessageBar(state: MessageBarState) {

    var startAnimation by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(key1 = state) {
        if (state.error != null) {
            errorMessage = when (state.error) {
                is SocketTimeoutException -> {
                    "Connection Timeout Exception"
                }

                is ConnectException -> {
                    "Internet Connection Exception"
                }

                else -> {
                    "${state.error.message}"
                }
            }
        }
        startAnimation = true
        delay(3000)
        startAnimation = false
    }

    AnimatedVisibility(
        visible = startAnimation && (state.error != null || state.message != null),
        enter = expandVertically(
            animationSpec = tween(300),
            expandFrom = Alignment.Top
        ),
        exit = shrinkVertically(
            animationSpec = tween(300),
            shrinkTowards = Alignment.Top
        )
    ) {
        Message(state = state, errorMessage = errorMessage)
    }
}

@Composable
fun Message(
    state: MessageBarState,
    modifier: Modifier = Modifier,
    errorMessage: String = ""
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = if (state.error != null) ErrorRed else InfoGreen)
            .padding(horizontal = 20.dp)
            .height(40.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (state.error != null) Icons.Default.Warning else Icons.Default.Check,
            contentDescription = "Message Bar Icon"
        )
        Divider(modifier = Modifier.width(12.dp), color = Color.Transparent)
        Text(
            text = if (state.error != null) errorMessage else state.message.toString(),

            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = MaterialTheme.typography.labelLarge.fontSize,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
    }
}

@Preview
@Composable
fun MessageBarPreview() {
    Message(state = MessageBarState(message = "Successfully updated."))
}

@Preview
@Composable
fun MessageBarErrorPreview() {
    Message(
        state = MessageBarState(error = SocketTimeoutException()),
        errorMessage = "Connection Timeout Exception."
    )
}