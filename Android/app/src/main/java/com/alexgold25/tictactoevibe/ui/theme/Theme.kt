package com.alexgold25.tictactoevibe.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Accent,
    secondary = AccentMuted,
    background = Background,
    surface = Surface,
    surfaceVariant = SurfaceHover,
    onPrimary = Text,
    onSecondary = Text,
    onBackground = Text,
    onSurface = Text
)

@Composable
fun TicTacToeVibeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
