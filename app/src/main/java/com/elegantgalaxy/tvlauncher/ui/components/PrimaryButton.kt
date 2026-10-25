package com.elegantgalaxy.tvlauncher.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.elegantgalaxy.tvlauncher.ui.theme.NavigationStyle
import com.elegantgalaxy.tvlauncher.utils.rememberTvFocusVisuals

/**
 * Primary button per the Figma spec: 236x145, 29 corner radius, fill
 * [MaterialTheme.colorScheme.primary] (#5C3BAB), 1px black inside stroke, no
 * shadow. Size defaults match Figma but can be overridden per call site.
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

    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .then(focusVisuals.modifier)
            .clip(shape)
            .background(MaterialTheme.colorScheme.primary)
            // Compose borders are drawn inside the bounds, matching Figma's "Inside" stroke.
            .border(BorderStroke(1.dp, Color.Black), shape)
            .clickable(
                interactionSource = focusVisuals.interactionSource,
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
