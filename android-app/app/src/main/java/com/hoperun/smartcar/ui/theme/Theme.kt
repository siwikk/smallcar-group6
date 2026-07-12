package com.hoperun.smartcar.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Accent,
    secondary = TextSecondary,
    background = Background,
    surface = CardBackground,
    onPrimary = AccentDark,
    onSecondary = TextPrimary,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    error = Danger
)

@Composable
fun SmartCarTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
