package com.aceshot.musicplayer.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = AuraPrimary,
    onPrimary = AuraOnPrimary,
    primaryContainer = AuraPrimaryContainer,
    onPrimaryContainer = AuraOnPrimaryContainer,
    secondary = AuraSecondary,
    onSecondary = AuraOnSecondary,
    secondaryContainer = AuraSecondaryContainer,
    onSecondaryContainer = AuraOnSecondaryContainer,
    tertiary = AuraTertiary,
    onTertiary = AuraOnTertiary,
    tertiaryContainer = AuraTertiaryContainer,
    onTertiaryContainer = AuraOnTertiaryContainer,
    background = AuraBackground,
    onBackground = AuraOnBackground,
    surface = AuraSurface,
    onSurface = AuraOnSurface,
    surfaceVariant = AuraSurfaceVariant,
    onSurfaceVariant = AuraOnSurfaceVariant,
    outline = AuraOutline
)

private val LightColorScheme = lightColorScheme(
    primary = AuraPrimaryLight,
    onPrimary = AuraOnPrimaryLight,
    primaryContainer = AuraPrimaryContainerLight,
    onPrimaryContainer = AuraOnPrimaryContainerLight,
    secondary = AuraSecondaryLight,
    onSecondary = AuraOnSecondaryLight,
    secondaryContainer = AuraSecondaryContainerLight,
    onSecondaryContainer = AuraOnSecondaryContainerLight,
    tertiary = AuraTertiaryLight,
    onTertiary = AuraOnTertiaryLight,
    tertiaryContainer = AuraTertiaryContainerLight,
    onTertiaryContainer = AuraOnTertiaryContainerLight,
    background = AuraBackgroundLight,
    onBackground = AuraOnBackgroundLight,
    surface = AuraSurfaceLight,
    onSurface = AuraOnSurfaceLight,
    surfaceVariant = AuraSurfaceVariantLight,
    onSurfaceVariant = AuraOnSurfaceVariantLight,
    outline = AuraOutlineLight
)

@Composable
fun AuraplayTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // Use Material You dynamic colors on Android 12+ if available
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
            // Transparent status bar for edge-to-edge feel
            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
