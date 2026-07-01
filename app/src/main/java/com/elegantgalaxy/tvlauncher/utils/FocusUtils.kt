package com.elegantgalaxy.tvlauncher.utils

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.scale
import androidx.compose.ui.unit.dp
import com.elegantgalaxy.tvlauncher.ui.theme.FocusRing
import kotlinx.coroutines.flow.collect

/**
 * Every focusable tile on this launcher (app icons, settings rows, carousel
 * cards) drives its focus affordance off the same [MutableInteractionSource]
 * pattern so "what's selected" looks identical everywhere a D-Pad can land.
 *
 * Scale (not elevation/shadow) is used for the focus affordance because
 * shadows are expensive to recompute per-frame on the 1-2GB RAM TV chipsets
 * this launcher targets.
 *
 * Usage: pass the returned [FocusVisuals.interactionSource] into the
 * focusable/clickable component (e.g. `Modifier.clickable(interactionSource = ...)`
 * or a TV-material `Card`'s `interactionSource` param), and apply
 * [FocusVisuals.modifier] on the same node for the scale/border effect.
 */
class FocusVisuals internal constructor(
    val interactionSource: MutableInteractionSource,
    val modifier: Modifier,
)

@Composable
fun rememberTvFocusVisuals(
    shape: Shape = RectangleShape,
    focusedScale: Float = 1.1f,
    unfocusedScale: Float = 1f,
    focusedBorderColor: Color = FocusRing,
    borderWidthDp: Int = 3,
): FocusVisuals {
    val interactionSource = remember { MutableInteractionSource() }
    var isFocused by remember { mutableStateOf(false) }

    androidx.compose.runtime.LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is FocusInteraction.Focus -> isFocused = true
                is FocusInteraction.Unfocus -> isFocused = false
            }
        }
    }

    val scale by animateFloatAsState(
        targetValue = if (isFocused) focusedScale else unfocusedScale,
        animationSpec = tween(durationMillis = 150),
        label = "tvFocusScale"
    )

    val modifier = Modifier
        .scale(scale)
        .then(
            if (isFocused) {
                Modifier.border(BorderStroke(borderWidthDp.dp, focusedBorderColor), shape)
            } else {
                Modifier
            }
        )

    return remember(scale, isFocused) { FocusVisuals(interactionSource, modifier) }
}
