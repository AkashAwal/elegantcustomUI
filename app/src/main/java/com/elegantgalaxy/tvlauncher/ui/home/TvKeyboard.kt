package com.elegantgalaxy.tvlauncher.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.elegantgalaxy.tvlauncher.ui.theme.SurfaceElevated2
import com.elegantgalaxy.tvlauncher.ui.theme.SurfaceElevated3
import com.elegantgalaxy.tvlauncher.utils.rememberTvFocusVisuals

private val ROWS = listOf("qwertyuiop", "asdfghjkl", "zxcvbnm")

/**
 * In-app D-Pad QWERTY keyboard, replacing reliance on the system IME so
 * search input stays fully D-Pad native (no soft keyboard popping up
 * unpredictably over the layout). Lowercase letters only — app-name
 * matching is case-insensitive, so shift/caps isn't needed for this
 * launcher's search — plus space, backspace, and search-submit.
 */
@Composable
fun TvKeyboard(
    onKeyPress: (Char) -> Unit,
    onBackspace: () -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier,
    firstKeyFocusRequester: FocusRequester? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(SurfaceElevated2)
            .padding(vertical = 16.dp, horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        ROWS.forEachIndexed { rowIndex, row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                modifier = Modifier.fillMaxWidth(),
            ) {
                row.forEachIndexed { charIndex, char ->
                    KeyButton(
                        label = char.toString(),
                        onClick = { onKeyPress(char) },
                        focusRequester = if (rowIndex == 0 && charIndex == 0) firstKeyFocusRequester else null,
                    )
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            modifier = Modifier.fillMaxWidth(),
        ) {
            KeyButton(label = "", onClick = onBackspace, icon = Icons.AutoMirrored.Filled.Backspace, widthDp = 64)
            KeyButton(label = "space", onClick = { onKeyPress(' ') }, widthDp = 280)
            KeyButton(label = "Search", onClick = onSearch, icon = Icons.Filled.Search, widthDp = 120, highlighted = true)
        }
    }
}

@Composable
private fun KeyButton(
    label: String,
    onClick: () -> Unit,
    icon: ImageVector? = null,
    widthDp: Int = 44,
    highlighted: Boolean = false,
    focusRequester: FocusRequester? = null,
) {
    val shape = RoundedCornerShape(8.dp)
    val focusVisuals = rememberTvFocusVisuals(shape = shape)

    Box(
        modifier = Modifier
            .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier)
            .then(focusVisuals.modifier)
            .width(widthDp.dp)
            .height(44.dp)
            .clip(shape)
            .background(if (highlighted) MaterialTheme.colorScheme.primary else SurfaceElevated3)
            .clickable(interactionSource = focusVisuals.interactionSource, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = if (label.isNotEmpty()) label else null,
                tint = if (highlighted) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
            )
        } else {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleSmall,
            )
        }
    }
}
