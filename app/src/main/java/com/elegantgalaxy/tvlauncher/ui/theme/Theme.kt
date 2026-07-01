package com.elegantgalaxy.tvlauncher.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.material3.MaterialTheme

// Dark scheme is the only one actually used on-device (TVs in the factory
// run this launcher exclusively), but a light scheme is kept for Compose
// Previews / future desktop-style debugging tools.
private val TvDarkColorScheme = darkColorScheme(
    primary = BrandPrimary,
    onPrimary = BrandOnPrimary,
    secondary = BrandSecondary,
    background = SurfaceBlack,
    surface = SurfaceElevated1,
    surfaceVariant = SurfaceElevated2,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    error = ErrorColor,
)

private val TvLightColorScheme = lightColorScheme(
    primary = BrandPrimary,
    onPrimary = BrandOnPrimary,
    secondary = BrandSecondary,
)

@Composable
fun ElegantGalaxyTvTheme(
    useDarkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (useDarkTheme) TvDarkColorScheme else TvLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = TvTypography,
        content = content
    )
}
