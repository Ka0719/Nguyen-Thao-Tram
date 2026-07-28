package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = SageGreenDark,
    onPrimary = DarkBackground,
    primaryContainer = SageGreenContainerDark,
    onPrimaryContainer = SageGreenDark,
    secondary = TerracottaDark,
    onSecondary = DarkBackground,
    secondaryContainer = TerracottaContainerDark,
    onSecondaryContainer = TerracottaDark,
    tertiary = CalmSkyDark,
    onTertiary = DarkBackground,
    tertiaryContainer = CalmSkyContainerDark,
    onTertiaryContainer = CalmSkyDark,
    background = DarkBackground,
    onBackground = GeometricSurface,
    surface = DarkSurface,
    onSurface = GeometricSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = GeometricSurface
)

private val LightColorScheme = lightColorScheme(
    primary = SageGreenPrimary,
    onPrimary = GeometricBackground,
    primaryContainer = SageGreenContainer,
    onPrimaryContainer = OnSageGreenContainer,
    secondary = TerracottaSecondary,
    onSecondary = GeometricBackground,
    secondaryContainer = TerracottaContainer,
    onSecondaryContainer = OnTerracottaContainer,
    tertiary = CalmSkyTertiary,
    onTertiary = GeometricBackground,
    tertiaryContainer = CalmSkyContainer,
    onTertiaryContainer = OnCalmSkyContainer,
    background = GeometricBackground,
    onBackground = SoftTextPrimary,
    surface = GeometricSurface,
    onSurface = SoftTextPrimary,
    surfaceVariant = GeometricSurfaceVariant,
    onSurfaceVariant = SoftTextSecondary
)

@Composable
fun MyApplicationTheme(
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
