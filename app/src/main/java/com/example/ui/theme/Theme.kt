package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = TaxiYellow,
    onPrimary = Obsidian900,
    primaryContainer = Obsidian700,
    onPrimaryContainer = TaxiYellow,
    secondary = TaxiYellowDark,
    onSecondary = Obsidian900,
    background = Obsidian900,
    onBackground = NeutralWhite,
    surface = Obsidian800,
    onSurface = NeutralWhite,
    surfaceVariant = Obsidian700,
    onSurfaceVariant = NeutralGray200,
    error = StatusRed,
    onError = NeutralWhite
)

private val LightColorScheme = lightColorScheme(
    primary = TaxiYellowDark,
    onPrimary = NeutralWhite,
    primaryContainer = TaxiYellowLight,
    onPrimaryContainer = Obsidian900,
    secondary = Obsidian800,
    onSecondary = NeutralWhite,
    background = NeutralGray50,
    onBackground = Obsidian900,
    surface = NeutralWhite,
    onSurface = Obsidian900,
    surfaceVariant = NeutralGray100,
    onSurfaceVariant = NeutralGray600,
    error = StatusRed,
    onError = NeutralWhite
)

@Composable
fun TaxigoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
