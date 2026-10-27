package com.elegantgalaxy.tvlauncher.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.elegantgalaxy.tvlauncher.ui.theme.BackdropGlowCrimson
import com.elegantgalaxy.tvlauncher.ui.theme.BackdropGlowPurple
import com.elegantgalaxy.tvlauncher.ui.theme.HomeBackground

private const val DESIGN_WIDTH = 1920f
private const val BLUR_SIGMA = 150f

/**
 * Page backdrop exactly as the Figma home frame builds it: flat #0D1020 with two
 * 15%-opacity discs under a 150 Gaussian blur — purple #5C3BAB (r918, centred
 * at 156,56) top-left and crimson #852848 (r654, centred at 1816,1088)
 * bottom-right, on the 1920x1080 canvas. The blur is approximated by a radial
 * gradient that stays solid to (r - 2σ) and fades out by (r + 2σ), which is
 * where a Gaussian-blurred disc edge actually lives; it scales with the screen
 * width so it matches on every panel size, and it's two draw calls (no blur
 * filter, which would be costly on the low-RAM TV chipsets this targets).
 */
@Composable
fun AppBackdrop(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(HomeBackground)
        val k = size.width / DESIGN_WIDTH
        glow(BackdropGlowPurple, center = Offset(156f * k, 56f * k), radius = 918f * k, blur = BLUR_SIGMA * k)
        glow(BackdropGlowCrimson, center = Offset(1816f * k, 1088f * k), radius = 654f * k, blur = BLUR_SIGMA * k)
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.glow(
    color: Color,
    center: Offset,
    radius: Float,
    blur: Float,
) {
    val outer = radius + 2 * blur
    val solid = ((radius - 2 * blur) / outer).coerceAtLeast(0f)
    val mid = (radius / outer)
    drawCircle(
        brush = Brush.radialGradient(
            solid to color.copy(alpha = 0.15f),
            mid to color.copy(alpha = 0.075f), // half strength at the disc edge, like a blurred step
            1f to color.copy(alpha = 0f),
            center = center,
            radius = outer,
        ),
        radius = outer,
        center = center,
    )
}
