package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// KIGO AI Futuristic Dark & Neon Red Palette
val KigoBackground = Color(0xFF07070A)
val KigoSurface = Color(0xFF0F0F16)
val KigoSurfaceElevated = Color(0xFF161622)
val KigoSurfaceCard = Color(0xFF1B1A28)
val KigoGlassBorder = Color(0x33FF2442)
val KigoGlassBorderSubtle = Color(0x22FFFFFF)

val KigoPrimaryRed = Color(0xFFFF2247)
val KigoNeonRed = Color(0xFFFF0D36)
val KigoAccentRed = Color(0xFFFF4D6D)
val KigoDeepRedContainer = Color(0xFF2E0A12)

val KigoSilver = Color(0xFFE2E8F0)
val KigoSilverMetallic = Color(0xFFCBD5E1)
val KigoMutedText = Color(0xFF94A3B8)
val KigoDarkText = Color(0xFF64748B)

val KigoRedGlow = Color(0x40FF1744)

// Metallic & Neon Gradients
val MetallicSilverBrush = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFFFFFFFF),
        Color(0xFFCBD5E1),
        Color(0xFF94A3B8),
        Color(0xFFE2E8F0)
    )
)

val KigoRedNeonBrush = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFFFF0D36),
        Color(0xFFFF4D6D),
        Color(0xFFE50914)
    )
)

val KigoCardGlassGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xD91E1D2D),
        Color(0xD913121E)
    )
)
