package com.example.artsyapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Light theme color scheme
private val LightColorScheme = lightColorScheme(
    primary = ArtsyBlue,
    onPrimary = Color.White,
    secondary = ArtsyPurple,
    onSecondary = Color.White,
    tertiary = ArtsyLightBlue,
    background = ArtsyBackground,
    onBackground = ArtsyDarkText,
    surface = ArtsyBackground,
    onSurface = ArtsyDarkText,
    surfaceVariant = ArtsyGreyBackground,
    onSurfaceVariant = ArtsyDarkText,
    primaryContainer = Color(0xFFD1E5FF)  // More blueish light blue color
)

// Dark theme color scheme - Updated to match the screenshot
private val DarkColorScheme = darkColorScheme(
    primary = ArtsyDarkPrimary,
    onPrimary = Color.White,
    secondary = ArtsyBlueDark,
    onSecondary = Color.White,
    tertiary = ArtsyLightBlueDark,
    background = ArtsyDarkBackground,
    onBackground = ArtsyDarkText,
    surface = ArtsyDarkItemBackground,
    onSurface = Color.White,
    surfaceVariant = ArtsyDarkItemBackground,
    onSurfaceVariant = Color.White,
    primaryContainer = ArtsyDarkNavyBlue,  // Changed to darker blue for dark mode
    onPrimaryContainer = Color.White,      // Updated text color to white for better visibility on dark blue
    secondaryContainer = ArtsyDarkItemBackground, // For list items
    onSecondaryContainer = Color.White       // Text on list items
)

// Define constants for light and dark theme status bar colors
val LightThemeStatusBarColor = Color(0xFFD1E5FF) // Light blue for light theme
val DarkThemeStatusBarColor = ArtsyLighterNavyBlue // Slightly lighter navy blue for dark mode

@Composable
fun ArtsyAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Use appropriate status bar color based on theme
            window.statusBarColor = if (darkTheme) DarkThemeStatusBarColor.toArgb() else LightThemeStatusBarColor.toArgb()
            // Set light status bar icons only for light theme
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}