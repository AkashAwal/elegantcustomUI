package com.elegantgalaxy.tvlauncher.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

private const val DESIGN_WIDTH_PX = 1920f
private const val DESIGN_HEIGHT_PX = 1080f

/**
 * Makes 1.dp / 1.sp inside [content] equal one pixel of the 1920x1080 Figma
 * frame, whatever the TV's real resolution or density bucket (720p, 1080p,
 * 4K, tvdpi, xhdpi...). Without it the same dp values render at wildly
 * different sizes per panel — the Figma numbers (96 tiles, 32 titles, 240x135
 * cards) are only "right" on a screen that is exactly 1920dp wide.
 *
 * The factor is the smaller of the width and height ratios, so the design
 * always fits the screen; the layouts fill the width, so a wider-than-16:9
 * panel just gets more room rather than letterboxing. Font scale from the
 * system settings is kept.
 */
@Composable
fun ProvideFigmaDensity(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val base = LocalDensity.current
    val density = remember(configuration, base.fontScale) {
        val metrics = context.resources.displayMetrics
        val scale = minOf(metrics.widthPixels / DESIGN_WIDTH_PX, metrics.heightPixels / DESIGN_HEIGHT_PX)
        Density(density = scale, fontScale = base.fontScale)
    }
    CompositionLocalProvider(LocalDensity provides density, content = content)
}
