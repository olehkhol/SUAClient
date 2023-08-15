package sky.tavrov.suaclient.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Gray200 = Color(0xFF819ca9)
val Gray500 = Color(0xFF546e7a)
val Gray700 = Color(0xFF29434e)
val Teal200 = Color(0xFF03DAC5)
val ErrorRed = Color(0xFFFF6C60)
val InfoGreen = Color(0xFF00C096)
val LoadingBlue = Color(0xFF1A73E8)

val ColorScheme.topAppBarContentColor: Color
    @Composable
    get() = if (isSystemInDarkTheme()) Color.LightGray else Color.White

val ColorScheme.topAppBarBackgroundColor: Color
    @Composable
    get() = if (isSystemInDarkTheme()) Color.Black else Gray500
