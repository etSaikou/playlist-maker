package com.saikou.playlistmaker.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = SettingsTextButtonDark,
    secondary = Blue,
    tertiary = White, // settings_draw_tint in dark
    secondaryContainer = White, // Search bar background in dark
    onSecondaryContainer = Black, // Search bar content in dark
    background = Black,
    surface = Black,
    onPrimary = Black,
    onSecondary = White,
    onBackground = White,
    onSurface = White
)

private val LightColorScheme = lightColorScheme(
    primary = SettingsTextButtonLight,
    secondary = Blue,
    tertiary = GrayText, // settings_draw_tint in light
    secondaryContainer = LightGray, // Search bar background in light
    onSecondaryContainer = GrayText, // Search bar hint/icons in light
    background = White,
    surface = White,
    onPrimary = White,
    onSecondary = White,
    onBackground = Black,
    onSurface = Black
)

@Composable
fun PlaylistMakerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
