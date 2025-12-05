package com.hfad.egypttour.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Light color scheme - Egypt Gold theme
 */
private val LightColorScheme = lightColorScheme(
    primary = EgyptGold,
    onPrimary = TextWhite,
    
    secondary = SoftPeach,
    onSecondary = TextBlack,
    
    background = SoftWhite,
    onBackground = TextBlack,
    
    surface = PureWhite,
    onSurface = TextBlack,
    
    error = ErrorRed
)

/**
 * Dark color scheme - Egypt Gold theme with dark backgrounds
 */
private val DarkColorScheme = darkColorScheme(
    primary = EgyptGold,
    onPrimary = TextBlack,
    
    secondary = EgyptGoldDark,
    onSecondary = DarkTextPrimary,
    
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    
    surfaceVariant = DarkSurfaceLight,
    onSurfaceVariant = DarkTextSecondary,
    
    error = ErrorRed
)

/**
 * Main theme composable for Egypt Tour app
 * @param darkTheme Whether to use dark theme (from user preference)
 * @param content App content
 */
@Composable
fun EgyptTourTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Status bar color matches theme
            window.statusBarColor = if (darkTheme) DarkBackground.toArgb() else EgyptGold.toArgb()
            // Status bar icons: white for dark mode, dark for light mode
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}