package com.nursingapp.ui.theme

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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    onPrimary = NeutralVariant99,
    primaryContainer = Purple90,
    onPrimaryContainer = Purple10,
    secondary = Teal40,
    onSecondary = NeutralVariant99,
    secondaryContainer = Teal90,
    onSecondaryContainer = Teal20,
    tertiary = Amber40,
    onTertiary = NeutralVariant99,
    tertiaryContainer = Amber90,
    onTertiaryContainer = Purple10,
    error = Error40,
    errorContainer = Error90,
    background = NeutralVariant99,
    onBackground = NeutralVariant20,
    surface = NeutralVariant99,
    onSurface = NeutralVariant20,
    surfaceVariant = Purple90,
    onSurfaceVariant = NeutralVariant20,
    outline = NeutralVariant50,
)

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    onPrimary = Purple20,
    primaryContainer = Purple30,
    onPrimaryContainer = Purple90,
    secondary = Teal80,
    onSecondary = Teal20,
    secondaryContainer = Teal20,
    onSecondaryContainer = Teal90,
    tertiary = Amber80,
    onTertiary = Purple10,
    tertiaryContainer = Purple10,
    onTertiaryContainer = Amber80,
    error = Error80,
    errorContainer = Error40,
    background = NeutralVariant20,
    onBackground = NeutralVariant80,
    surface = NeutralVariant20,
    onSurface = NeutralVariant80,
    surfaceVariant = NeutralVariant20,
    onSurfaceVariant = NeutralVariant80,
    outline = NeutralVariant50,
)

@Composable
fun NursingAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
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
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = NursingAppTypography,
        content = content,
    )
}
