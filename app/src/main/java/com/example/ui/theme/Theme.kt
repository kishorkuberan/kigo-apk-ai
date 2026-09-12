package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val KigoDarkColorScheme = darkColorScheme(
    primary = KigoPrimaryRed,
    onPrimary = Color.White,
    primaryContainer = KigoDeepRedContainer,
    onPrimaryContainer = Color(0xFFFFD9DF),
    secondary = KigoSilver,
    onSecondary = Color(0xFF0F0F16),
    secondaryContainer = Color(0xFF232332),
    onSecondaryContainer = KigoSilverMetallic,
    tertiary = KigoAccentRed,
    onTertiary = Color.White,
    background = KigoBackground,
    onBackground = KigoSilver,
    surface = KigoSurface,
    onSurface = KigoSilver,
    surfaceVariant = KigoSurfaceElevated,
    onSurfaceVariant = KigoMutedText,
    outline = KigoGlassBorder,
    outlineVariant = KigoGlassBorderSubtle
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false, // Preserve our custom KIGO AI futuristic branding
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = KigoDarkColorScheme,
        typography = Typography,
        content = content
    )
}
