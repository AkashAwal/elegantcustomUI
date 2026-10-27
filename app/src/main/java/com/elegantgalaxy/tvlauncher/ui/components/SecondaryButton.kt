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
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import com.elegantgalaxy.tvlauncher.ui.theme.NavigationStyle
import com.elegantgalaxy.tvlauncher.ui.theme.AccentLavender
import com.elegantgalaxy.tvlauncher.ui.theme.SecondaryButtonFill
import com.elegantgalaxy.tvlauncher.ui.theme.SecondaryButtonFocusedFill
import com.elegantgalaxy.tvlauncher.ui.theme.SecondaryButtonPressedFill
import com.elegantgalaxy.tvlauncher.ui.theme.SecondaryButtonStroke

/**
 * Secondary button per the Figma spec: 236x145, 29 corner radius, #171A2C
 * fill, 1px inside stroke of white at 8% opacity. Focused (D-Pad): #222540
 * fill with the same 2px #9F7FDB stroke as the primary button's focus state.
 * Pressed: #2E3050 fill, no stroke.
 */
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    width: Dp = 236.dp,
    height: Dp = 145.dp,
    cornerRadius: Dp = 29.dp,
    horizontalPadding: Dp = 0.dp,
    textStyle: TextStyle = NavigationStyle,
    /** Resting fill; focused/pressed keep their own fills. Defaults to the solid secondary fill. */
    fill: Color = SecondaryButtonFill,
) {
    val shape = RoundedCornerShape(cornerRadius)
    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()
    val pressed by interactionSource.collectIsPressedAsState()

    Box(
        modifier = modifier
            // Dp.Unspecified width = wrap the label (+ horizontalPadding), e.g. the hero's More Info button.
            .then(if (width.isSpecified) Modifier.width(width) else Modifier)
            .height(height)
            .clip(shape)
            .background(
                when {
                    pressed -> SecondaryButtonPressedFill
                    focused -> SecondaryButtonFocusedFill
                    else -> fill
                },
            )
            // Pressed state has no stroke in Figma.
            .then(
                when {
                    pressed -> Modifier
                    focused -> Modifier.border(BorderStroke(2.dp, AccentLavender), shape)
                    else -> Modifier.border(BorderStroke(1.dp, SecondaryButtonStroke), shape)
                },
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = horizontalPadding),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = textStyle,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
