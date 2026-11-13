package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val CheapTravelColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = DeepNightBg,
    primaryContainer = CyanTint,
    onPrimaryContainer = NeonCyan,
    secondary = NeonPink,
    onSecondary = TextPrimary,
    secondaryContainer = PinkTint,
    onSecondaryContainer = NeonPink,
    tertiary = NeonGreen,
    onTertiary = DeepNightBg,
    tertiaryContainer = GreenTint,
    onTertiaryContainer = NeonGreen,
    background = DeepNightBg,
    onBackground = TextPrimary,
    surface = SurfaceNavy,
    onSurface = TextPrimary,
    surfaceVariant = CardElevated,
    onSurfaceVariant = TextSecondary,
    surfaceContainerLowest = DeepNightBg,
    surfaceContainerLow = SurfaceNavy,
    surfaceContainer = CardElevated,
    surfaceContainerHigh = CardHighest,
    outline = CardBorder,
    outlineVariant = Color(0xFF1A2438),
    error = NeonPink,
    onError = TextPrimary
)

private val CheapTravelShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(18.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

@Composable
fun CheapTravelTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CheapTravelColorScheme,
        typography = CheapTravelTypography,
        shapes = CheapTravelShapes,
        content = content
    )
}
