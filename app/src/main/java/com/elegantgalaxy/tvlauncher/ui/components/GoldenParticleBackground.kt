package com.elegantgalaxy.tvlauncher.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.sin
import kotlin.random.Random

private data class Particle(
    val seedX: Float,
    val seedY: Float,
    val speed: Float,
    val radiusPx: Float,
    val phaseOffset: Float,
)

private const val PARTICLE_COUNT = 32
private val GoldParticleColor = Color(0xFFFFD54A)

/**
 * A handful of slow-drifting golden dots behind the launcher content.
 *
 * Deliberately cheap: fixed particle count, plain `drawCircle` calls (no
 * blur/shader/bitmap work), and particle positions are a pure function of
 * one animated `time` float rather than per-particle Compose state — so
 * only the Canvas node redraws each frame, nothing else recomposes. This
 * matters on the 1-2GB RAM TV chipsets this launcher targets; see
 * [com.elegantgalaxy.tvlauncher.utils.FocusUtils] for the same reasoning
 * applied to focus animations.
 */
@Composable
fun GoldenParticleBackground(modifier: Modifier = Modifier) {
    val particles = remember {
        val random = Random(seed = 42)
        List(PARTICLE_COUNT) {
            Particle(
                seedX = random.nextFloat(),
                seedY = random.nextFloat(),
                speed = 0.015f + random.nextFloat() * 0.02f,
                radiusPx = 3f + random.nextFloat() * 6f,
                phaseOffset = random.nextFloat() * 1000f,
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "goldenParticles")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 60_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "particleTime",
    )

    Canvas(modifier = modifier) {
        particles.forEach { particle ->
            val t = time + particle.phaseOffset
            // Drift upward and loop back to the bottom; small horizontal sway.
            val y = 1f - ((particle.seedY + t * particle.speed) % 1f)
            val x = (particle.seedX + 0.03f * sin(t * 6.28f + particle.phaseOffset)).mod(1f)
            val twinkle = 0.5f + 0.5f * sin(t * 12.56f + particle.phaseOffset)
            val alpha = 0.12f + 0.28f * twinkle

            drawCircle(
                color = GoldParticleColor.copy(alpha = alpha),
                radius = particle.radiusPx,
                center = Offset(x * size.width, y * size.height),
            )
        }
    }
}
