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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.elegantgalaxy.tvlauncher.ui.theme.NavigationStyle
import com.elegantgalaxy.tvlauncher.ui.theme.PrimaryButtonFocusedStroke
import com.elegantgalaxy.tvlauncher.ui.theme.SecondaryButtonFill
import com.elegantgalaxy.tvlauncher.ui.theme.SecondaryButtonFocusedFill
import com.elegantgalaxy.tvlauncher.ui.theme.SecondaryButtonStroke

/**
 * Secondary button per the Figma spec: 236x145, 29 corner radius, #171A2C
 * fill, 1px inside stroke of white at 8% opacity. Focused (D-Pad): #222540
 * fill with the same 2px #9F7FDB stroke as the primary button's focus state.
 * Pressed state still needs to be added once designed.
 */
@Composable
fun SecondaryButton(
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
                    pressed -> SecondaryButtonPressedFill
                    focused -> SecondaryButtonFocusedFill
                    else -> SecondaryButtonFill
                },
            )
            // Pressed state has no stroke in Figma.
            .then(
                when {
                    pressed -> Modifier
                    focused -> Modifier.border(BorderStroke(2.dp, PrimaryButtonFocusedStroke), shape)
                    else -> Modifier.border(BorderStroke(1.dp, SecondaryButtonStroke), shape)
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
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
