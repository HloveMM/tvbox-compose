package com.tvbox.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val Bg = Color(0xFF08090D)
val Surface1 = Color(0xFF14161D)
val Surface2 = Color(0xFF1B1E27)
val Text1 = Color(0xFFEEF1F7)
val Text2 = Color(0x99EEF1F7)
val Text3 = Color(0x5CEEF1F7)
val Accent = Color(0xFFFF4D3D)
val Accent2 = Color(0xFFFF9142)
val OkColor = Color(0xFF3DDC97)
val WarnColor = Color(0xFFFFB347)
val AccentBrush = Brush.horizontalGradient(listOf(Accent, Accent2))

private val scheme = darkColorScheme(
    primary = Accent,
    onPrimary = Color.White,
    secondary = Accent2,
    background = Bg,
    onBackground = Text1,
    surface = Surface1,
    onSurface = Text1,
    surfaceVariant = Surface2,
    onSurfaceVariant = Text2,
)

@Composable
fun TVBoxTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = scheme, content = content)
}
