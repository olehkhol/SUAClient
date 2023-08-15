package sky.tavrov.suaclient.presentation.screen.login

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import sky.tavrov.suaclient.ui.theme.topAppBarBackgroundColor
import sky.tavrov.suaclient.ui.theme.topAppBarContentColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginTopBar() {
    TopAppBar(
        title = {
            Text(text = "Sign in", color = MaterialTheme.colorScheme.topAppBarContentColor)
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.topAppBarBackgroundColor)
    )
}

@Preview
@Composable
fun LoginTopBarPreview() {
    LoginTopBar()
}