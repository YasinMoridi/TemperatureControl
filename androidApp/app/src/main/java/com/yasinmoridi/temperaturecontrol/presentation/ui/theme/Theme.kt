package com.yasinmoridi.temperaturecontrol.presentation.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AccentCyan,
    secondary = SurfaceColor,
    tertiary = AccentRed,
    background = BgColor,
    surface = SurfaceColor,
    onPrimary = Color.Black,
    onSecondary = TextPrimary,
    onTertiary = Color.White,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
)

@Composable
fun TemperatureControlTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
