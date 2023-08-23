package sky.tavrov.suaclient.presentation.screen.profile

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import sky.tavrov.suaclient.R
import sky.tavrov.suaclient.ui.theme.topAppBarBackgroundColor
import sky.tavrov.suaclient.ui.theme.topAppBarContentColor


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTopBar(
    onSave: () -> Unit,
    onDeleteAllConfirmed: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "Profile",
                color = MaterialTheme.colorScheme.topAppBarContentColor
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.topAppBarBackgroundColor
        ),
        actions = {
            ProfileTopBarActions(
                onSave = onSave,
                onDeleteAllConfirmed = onDeleteAllConfirmed
            )
        }
    )
}

@Composable
fun ProfileTopBarActions(
    onSave: () -> Unit,
    onDeleteAllConfirmed: () -> Unit
) {
    SaveActions(onSave = onSave)
    DeleteAllAction(onDeleteAllConfirmed = onDeleteAllConfirmed)
}

@Composable
fun SaveActions(onSave: () -> Unit) {
    IconButton(onClick = onSave) {
        Icon(
            painter = painterResource(id = R.drawable.ic_save),
            contentDescription = "Save Icon",
            tint = MaterialTheme.colorScheme.topAppBarContentColor
        )
    }
}

@Composable
fun DeleteAllAction(onDeleteAllConfirmed: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = { expanded = true }) {
        Icon(
            painter = painterResource(id = R.drawable.ic_vertical_menu),
            contentDescription = "Vertical Menu",
            tint = MaterialTheme.colorScheme.topAppBarContentColor
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = "Delete Account",
                        style = MaterialTheme.typography.titleSmall
                    )
                },
                onClick = {
                    expanded = false
                    onDeleteAllConfirmed()
                }
            )
        }
    }
}

@Preview
@Composable
fun ProfileTopBarPreview() {
    ProfileTopBar(onSave = {}, onDeleteAllConfirmed = {})
}