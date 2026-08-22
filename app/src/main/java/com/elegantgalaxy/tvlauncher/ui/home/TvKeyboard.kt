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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.elegantgalaxy.tvlauncher.ui.theme.SurfaceElevated2
import com.elegantgalaxy.tvlauncher.ui.theme.SurfaceElevated3
import com.elegantgalaxy.tvlauncher.utils.rememberTvFocusVisuals

private val NUMBER_ROW = "1234567890"

// "?" sits below "p", "@" sits below "a", and "," "." sit below "?" (comma
// to its left) — standard mobile-keyboard punctuation placement, requested
// explicitly. Only applies to the letters layout; symbols mode has its own
// full punctuation set instead.
private val LETTER_ROWS = listOf("qwertyuiop", "asdfghjkl?", "@zxcvbnm,.")
private val SYMBOL_ROWS = listOf("!@#$%^&*()", "-_=+[]{}\\|", ";:'\",.<>/?")

/**
 * In-app D-Pad keyboard, replacing reliance on the system IME so search
 * input stays fully D-Pad native. Full layout: numbers row, three
 * letter/symbol rows, and a space-bar row, flanked by a left action column
 * (language, symbols toggle, caps lock, hide keyboard) and a right action
 * column (backspace, Done, mic, clear-all, cursor chevrons).
 */
@Composable
fun TvKeyboard(
    capsLock: Boolean,
    onToggleCaps: () -> Unit,
    symbolsMode: Boolean,
    onToggleSymbols: () -> Unit,
    onCharPress: (Char) -> Unit,
    onBackspace: () -> Unit,
    onClearAll: () -> Unit,
    onMoveCursor: (Int) -> Unit,
    onDone: () -> Unit,
    onMicPress: () -> Unit,
    onHideKeyboard: () -> Unit,
    modifier: Modifier = Modifier,
    firstKeyFocusRequester: FocusRequester? = null,
) {
    val rows = if (symbolsMode) SYMBOL_ROWS else LETTER_ROWS

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(SurfaceElevated2)
            .padding(vertical = 6.dp, horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        // Numbers row: language label (left) ... digits ... backspace (right)
        KeyRow {
            LabelKey(text = "ENG")
            NUMBER_ROW.forEach { digit ->
                CharKey(
                    label = digit.toString(),
                    onClick = { onCharPress(digit) },
                    focusRequester = if (digit == NUMBER_ROW.first()) firstKeyFocusRequester else null,
                )
            }
            IconKey(icon = Icons.AutoMirrored.Filled.Backspace, contentDescription = "Backspace", onClick = onBackspace)
        }

        // Row 1: symbols/ABC toggle (left) ... qwertyuiop ... Done (right, green)
        KeyRow {
            TextActionKey(text = if (symbolsMode) "ABC" else "!#1", onClick = onToggleSymbols)
            rows[0].forEach { char ->
                CharKey(label = displayChar(char, capsLock), onClick = { onCharPress(effectiveChar(char, capsLock)) })
            }
            TextActionKey(text = "Done", onClick = onDone, highlighted = true)
        }

        // Row 2: caps lock (left) ... asdfghjkl ... mic (right)
        KeyRow {
            IconKey(
                icon = Icons.Filled.KeyboardArrowUp,
                contentDescription = "Caps lock",
                onClick = onToggleCaps,
                active = capsLock,
            )
            rows[1].forEach { char ->
                CharKey(label = displayChar(char, capsLock), onClick = { onCharPress(effectiveChar(char, capsLock)) })
            }
            IconKey(icon = Icons.Filled.Mic, contentDescription = "Voice search", onClick = onMicPress)
        }

        // Row 3: hide keyboard (left) ... zxcvbnm ... clear all (right)
        KeyRow {
            IconKey(icon = Icons.Filled.KeyboardArrowDown, contentDescription = "Hide keyboard", onClick = onHideKeyboard)
            rows[2].forEach { char ->
                CharKey(label = displayChar(char, capsLock), onClick = { onCharPress(effectiveChar(char, capsLock)) })
            }
            TextActionKey(text = "Clear", onClick = onClearAll)
        }

        // Row 4: space bar, flanked by cursor chevrons on the right
        KeyRow {
            SpaceKey(onClick = { onCharPress(' ') }, modifier = Modifier.weight(1f))
            IconKey(icon = Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Move cursor left", onClick = { onMoveCursor(-1) })
            IconKey(icon = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Move cursor right", onClick = { onMoveCursor(1) })
        }
    }
}

private fun displayChar(char: Char, capsLock: Boolean): String =
    (if (capsLock) char.uppercaseChar() else char).toString()

private fun effectiveChar(char: Char, capsLock: Boolean): Char =
    if (capsLock) char.uppercaseChar() else char

@Composable
private fun KeyRow(content: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
        content = content,
    )
}

@Composable
private fun LabelKey(text: String) {
    Box(
        modifier = Modifier
            .width(34.dp)
            .height(28.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun CharKey(label: String, onClick: () -> Unit, focusRequester: FocusRequester? = null) {
    val shape = RoundedCornerShape(6.dp)
    val focusVisuals = rememberTvFocusVisuals(shape = shape)

    Box(
        modifier = Modifier
            .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier)
            .then(focusVisuals.modifier)
            .width(28.dp)
            .height(28.dp)
            .clip(shape)
            .background(SurfaceElevated3)
            .clickable(interactionSource = focusVisuals.interactionSource, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = label, color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.titleSmall)
    }
}

@Composable
private fun IconKey(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    active: Boolean = false,
) {
    val shape = RoundedCornerShape(6.dp)
    val focusVisuals = rememberTvFocusVisuals(shape = shape)

    Box(
        modifier = Modifier
            .then(focusVisuals.modifier)
            .width(34.dp)
            .height(28.dp)
            .clip(shape)
            .background(if (active) MaterialTheme.colorScheme.primary else SurfaceElevated3)
            .clickable(interactionSource = focusVisuals.interactionSource, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (active) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun TextActionKey(text: String, onClick: () -> Unit, highlighted: Boolean = false) {
    val shape = RoundedCornerShape(6.dp)
    val focusVisuals = rememberTvFocusVisuals(shape = shape)
    val greenDone = Color(0xFF2E7D32)

    Box(
        modifier = Modifier
            .then(focusVisuals.modifier)
            // Same width as LabelKey/IconKey so every row's flanking column
            // lines up exactly — a text button here would otherwise be
            // wider and throw off column alignment between rows.
            .width(34.dp)
            .height(28.dp)
            .clip(shape)
            .background(if (highlighted) greenDone else SurfaceElevated3)
            .clickable(interactionSource = focusVisuals.interactionSource, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = if (highlighted) Color.White else MaterialTheme.colorScheme.onSurface,
            fontWeight = if (highlighted) FontWeight.Bold else FontWeight.Normal,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

@Composable
private fun SpaceKey(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(6.dp)
    val focusVisuals = rememberTvFocusVisuals(shape = shape)

    Box(
        modifier = modifier
            .then(focusVisuals.modifier)
            .height(28.dp)
            .clip(shape)
            .background(SurfaceElevated3)
            .clickable(interactionSource = focusVisuals.interactionSource, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "space", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelMedium)
    }
}
