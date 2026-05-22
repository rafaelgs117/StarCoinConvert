package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val CosmicColorScheme = darkColorScheme(
    primary = GoldStar,
    onPrimary = BgDark,
    secondary = BgCard,
    onSecondary = WhiteText,
    background = BgDark,
    onBackground = WhiteText,
    surface = BgCard,
    onSurface = WhiteText,
    surfaceVariant = BgDark,
    onSurfaceVariant = MutedText
)

@Composable
fun StarCoinTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CosmicColorScheme,
        typography = Typography,
        content = content
    )
}
