package com.elegantgalaxy.tvlauncher.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.elegantgalaxy.tvlauncher.ui.theme.NavigationStyle
import com.elegantgalaxy.tvlauncher.ui.theme.PrimaryButtonFocusedFill
import com.elegantgalaxy.tvlauncher.ui.theme.AccentLavender
import com.elegantgalaxy.tvlauncher.ui.theme.PrimaryButtonPressedFill

/**
 * Primary button per the Figma spec: 236x145, 29 corner radius, fill
 * [MaterialTheme.colorScheme.primary] (#5C3BAB), 1px black inside stroke, no
 * shadow. Focused (D-Pad) state per Figma: lighter #6C4AC4 fill, 2px #9F7FDB
 * inside stroke, no scale change. Size defaults match Figma but can be overridden per call site.
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    width: Dp = 236.dp,
    height: Dp = 145.dp,
) {
    val shape = RoundedCornerShape(29.dp)
    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()
    val pressed by interactionSource.collectIsPressedAsState()

    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(shape)
            .background(
                when {
                    pressed -> PrimaryButtonPressedFill
                    focused -> PrimaryButtonFocusedFill
                    else -> MaterialTheme.colorScheme.primary
                },
            )
            // Compose borders are drawn inside the bounds, matching Figma's "Inside" stroke.
            // Pressed state has no stroke in Figma.
            .then(
                when {
                    pressed -> Modifier
                    focused -> Modifier.border(BorderStroke(2.dp, AccentLavender), shape)
                    else -> Modifier.border(BorderStroke(1.dp, Color.Black), shape)
                },
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = NavigationStyle,
            color = MaterialTheme.colorScheme.onPrimary,
        )
    }
}
