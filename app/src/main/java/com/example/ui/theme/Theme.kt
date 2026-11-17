package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Immutable
data class CheapTravelColors(
    val deepNightBg: Color,
    val surfaceNavy: Color,
    val cardElevated: Color,
    val cardHighest: Color,
    val cardBorder: Color,
    val disabledCellBg: Color,
    val disabledCellBorder: Color,
    val neonCyan: Color,
    val neonPink: Color,
    val neonGreen: Color,
    val neonGold: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val cyanTint: Color,
    val pinkTint: Color,
    val greenTint: Color,
    val goldTint: Color,
    val onAccent: Color,
    val onPinkButton: Color,
    val isDark: Boolean
)

val LightCheapTravelColors = CheapTravelColors(
    deepNightBg = LightBg,
    surfaceNavy = LightSurface,
    cardElevated = LightCardElevated,
    cardHighest = LightCardHighest,
    cardBorder = LightCardBorder,
    disabledCellBg = LightDisabledCellBg,
    disabledCellBorder = LightDisabledCellBorder,
    neonCyan = LightPrimaryCyan,
    neonPink = LightAccentPink,
    neonGreen = LightOfferGreen,
    neonGold = LightHighlightGold,
    textPrimary = LightTextPrimary,
    textSecondary = LightTextSecondary,
    textMuted = LightTextMuted,
    cyanTint = LightCyanTint,
    pinkTint = LightPinkTint,
    greenTint = LightGreenTint,
    goldTint = LightGoldTint,
    onAccent = Color.White,
    onPinkButton = Color.White,
    isDark = false
)

val DarkCheapTravelColors = CheapTravelColors(
    deepNightBg = DarkDeepNightBg,
    surfaceNavy = DarkSurfaceNavy,
    cardElevated = DarkCardElevated,
    cardHighest = DarkCardHighest,
    cardBorder = DarkCardBorder,
    disabledCellBg = DarkDisabledCellBg,
    disabledCellBorder = DarkDisabledCellBorder,
    neonCyan = DarkNeonCyan,
    neonPink = DarkNeonPink,
    neonGreen = DarkNeonGreen,
    neonGold = DarkNeonGold,
    textPrimary = DarkTextPrimary,
    textSecondary = DarkTextSecondary,
    textMuted = DarkTextMuted,
    cyanTint = DarkCyanTint,
    pinkTint = DarkPinkTint,
    greenTint = DarkGreenTint,
    goldTint = DarkGoldTint,
    onAccent = DarkDeepNightBg,
    onPinkButton = DarkTextPrimary,
    isDark = true
)

val LocalCheapTravelColors = staticCompositionLocalOf { LightCheapTravelColors }

object AppTheme {
    val colors: CheapTravelColors
        @Composable
        @ReadOnlyComposable
        get() = LocalCheapTravelColors.current
}

private val DarkMaterialColorScheme = darkColorScheme(
    primary = DarkNeonCyan,
    onPrimary = DarkDeepNightBg,
    primaryContainer = DarkCyanTint,
    onPrimaryContainer = DarkNeonCyan,
    secondary = DarkNeonPink,
    onSecondary = DarkTextPrimary,
    secondaryContainer = DarkPinkTint,
    onSecondaryContainer = DarkNeonPink,
    tertiary = DarkNeonGreen,
    onTertiary = DarkDeepNightBg,
    tertiaryContainer = DarkGreenTint,
    onTertiaryContainer = DarkNeonGreen,
    background = DarkDeepNightBg,
    onBackground = DarkTextPrimary,
    surface = DarkSurfaceNavy,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkCardElevated,
    onSurfaceVariant = DarkTextSecondary,
    surfaceContainerLowest = DarkDeepNightBg,
    surfaceContainerLow = DarkSurfaceNavy,
    surfaceContainer = DarkCardElevated,
    surfaceContainerHigh = DarkCardHighest,
    outline = DarkCardBorder,
    outlineVariant = Color(0xFF1A2438),
    error = DarkNeonPink,
    onError = DarkTextPrimary
)

private val LightMaterialColorScheme = lightColorScheme(
    primary = LightPrimaryCyan,
    onPrimary = Color.White,
    primaryContainer = LightCyanTint,
    onPrimaryContainer = LightPrimaryCyan,
    secondary = LightAccentPink,
    onSecondary = Color.White,
    secondaryContainer = LightPinkTint,
    onSecondaryContainer = LightAccentPink,
    tertiary = LightOfferGreen,
    onTertiary = Color.White,
    tertiaryContainer = LightGreenTint,
    onTertiaryContainer = LightOfferGreen,
    background = LightBg,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightCardElevated,
    onSurfaceVariant = LightTextSecondary,
    surfaceContainerLowest = LightBg,
    surfaceContainerLow = LightSurface,
    surfaceContainer = LightCardElevated,
    surfaceContainerHigh = LightCardHighest,
    outline = LightCardBorder,
    outlineVariant = LightDisabledCellBorder,
    error = LightAccentPink,
    onError = Color.White
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
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val palette = if (darkTheme) DarkCheapTravelColors else LightCheapTravelColors
    val materialScheme = if (darkTheme) DarkMaterialColorScheme else LightMaterialColorScheme

    CompositionLocalProvider(LocalCheapTravelColors provides palette) {
        MaterialTheme(
            colorScheme = materialScheme,
            typography = CheapTravelTypography,
            shapes = CheapTravelShapes,
            content = content
        )
    }
}
