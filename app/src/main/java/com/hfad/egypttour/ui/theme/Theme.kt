package com.hfad.egypttour.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

//private val DarkColorScheme = darkColorScheme(
//    primary = Purple80,
//    secondary = PurpleGrey80,
//    tertiary = Pink80
//)

//private val LightColorScheme = lightColorScheme(
//    primary = Purple40,
//    secondary = PurpleGrey40,
//    tertiary = Pink40
//
//    /* Other default colors to override
//    background = Color(0xFFFFFBFE),
//    surface = Color(0xFFFFFBFE),
//    onPrimary = Color.White,
//    onSecondary = Color.White,
//    onTertiary = Color.White,
//    onBackground = Color(0xFF1C1B1F),
//    onSurface = Color(0xFF1C1B1F),
//    */
//)
private val LightColorScheme = lightColorScheme(
    primary = EgyptGold,
    onPrimary = TextWhite, // Text on Gold buttons

    secondary = SoftPeach,
    onSecondary = TextBlack,

    background = SoftWhite,
    onBackground = TextBlack,

    surface = PureWhite,
    onSurface = TextBlack,

    error = ErrorRed
)
@Composable
fun EgyptTourTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    // WE TURN THIS OFF to ensure your Gold color is used, not the user's wallpaper color
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme
    // If you really want dark mode, create a DarkColorScheme and use logic here
    // But for MVP, stick to one scheme to ensure consistency.

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // This makes the top System Status Bar (Battery/Wifi) Gold to match your header
            window.statusBarColor = EgyptGold.toArgb()
            // Makes status bar icons white
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}