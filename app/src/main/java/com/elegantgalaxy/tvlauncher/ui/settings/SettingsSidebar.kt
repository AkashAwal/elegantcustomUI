package com.elegantgalaxy.tvlauncher.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import com.elegantgalaxy.tvlauncher.ui.theme.DisplayMode
import com.elegantgalaxy.tvlauncher.ui.theme.SurfaceElevated1
import com.elegantgalaxy.tvlauncher.ui.theme.SurfaceElevated2
import com.elegantgalaxy.tvlauncher.ui.theme.SurfaceElevated3
import com.elegantgalaxy.tvlauncher.utils.DisplayModeStore
import com.elegantgalaxy.tvlauncher.utils.QuickSettingsStore
import com.elegantgalaxy.tvlauncher.utils.SettingsActions
import com.elegantgalaxy.tvlauncher.utils.rememberTvFocusVisuals

/**
 * Quick-settings sidebar: opens as an overlay next to the nav rail instead
 * of navigating to a full page (see [com.elegantgalaxy.tvlauncher.navigation.TvLauncherNavGraph]).
 * Every middle item is an inline cycle switcher — no drill-down screens.
 */
@Composable
fun SettingsSidebar(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        QuickSettingsStore.ensureInitialized(context)
        DisplayModeStore.current(context)
    }

    val displayMode by DisplayModeStore.mode.collectAsState()
    val soundMode by QuickSettingsStore.soundMode.collectAsState()
    val soundOutput by QuickSettingsStore.soundOutput.collectAsState()
    val sleepTimer by QuickSettingsStore.sleepTimer.collectAsState()
    val eyeCareEnabled by QuickSettingsStore.eyeCareEnabled.collectAsState()
    var brightnessPercent by remember { mutableIntStateOf(SettingsActions.getBrightnessPercent(context)) }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(300.dp)
            .background(SurfaceElevated1)
            .padding(16.dp),
    ) {
        FullWidthRow(label = "Support", icon = Icons.Filled.SupportAgent, onClick = {})

        Spacer(Modifier.height(20.dp))

        val tileRows = listOf(
            "Picture Mode" to (displayMode.label to { delta: Int ->
                DisplayModeStore.setMode(context, cycle(DisplayMode.entries, displayMode, delta))
            }),
            "Brightness" to ("$brightnessPercent%" to { delta: Int ->
                if (delta > 0) SettingsActions.increaseBrightness(context) else SettingsActions.decreaseBrightness(context)
                brightnessPercent = SettingsActions.getBrightnessPercent(context)
            }),
            "Sound Mode" to (soundMode.label to { delta: Int ->
                QuickSettingsStore.setSoundMode(context, cycle(SoundMode.entries, soundMode, delta))
            }),
            "Sound Output" to (soundOutput.label to { delta: Int ->
                QuickSettingsStore.setSoundOutput(context, cycle(SoundOutput.entries, soundOutput, delta))
            }),
            "Sleep Timer" to (sleepTimer.label to { delta: Int ->
                QuickSettingsStore.setSleepTimer(context, cycle(SleepTimer.entries, sleepTimer, delta))
            }),
            "Eye Care Mode" to ((if (eyeCareEnabled) "On" else "Off") to { _: Int ->
                QuickSettingsStore.setEyeCareEnabled(context, !eyeCareEnabled)
            }),
        )

        tileRows.chunked(2).forEach { pair ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                pair.forEach { (label, valueAndStep) ->
                    val (value, onStep) = valueAndStep
                    QuickSettingTile(
                        label = label,
                        value = value,
                        onPrev = { onStep(-1) },
                        onNext = { onStep(1) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        Spacer(Modifier.weight(1f))

        FullWidthRow(label = "Contact Us", icon = Icons.Filled.ContactPhone, onClick = {})
    }
}

private fun <T> cycle(entries: List<T>, current: T, delta: Int): T {
    val index = entries.indexOf(current)
    val size = entries.size
    return entries[((index + delta) % size + size) % size]
}

@Composable
private fun FullWidthRow(label: String, icon: ImageVector, onClick: () -> Unit) {
    val shape = RoundedCornerShape(10.dp)
    val focusVisuals = rememberTvFocusVisuals(shape = shape)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .then(focusVisuals.modifier)
            .clip(shape)
            .background(SurfaceElevated2)
            .clickable(interactionSource = focusVisuals.interactionSource, indication = null, onClick = onClick)
            .padding(16.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().align(Alignment.Center),
        )
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.align(Alignment.CenterEnd),
        )
    }
}

@Composable
private fun QuickSettingTile(
    label: String,
    value: String,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(10.dp)

    Column(
        modifier = modifier
            .clip(shape)
            .background(SurfaceElevated2)
            .padding(14.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StepButton(icon = Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous $label", onClick = onPrev)
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
            )
            StepButton(icon = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next $label", onClick = onNext)
        }
    }
}

@Composable
private fun StepButton(icon: ImageVector, contentDescription: String, onClick: () -> Unit) {
    val shape = RoundedCornerShape(8.dp)
    val focusVisuals = rememberTvFocusVisuals(shape = shape)

    Row(
        modifier = Modifier
            .size(32.dp)
            .then(focusVisuals.modifier)
            .clip(shape)
            .background(SurfaceElevated3)
            .clickable(interactionSource = focusVisuals.interactionSource, indication = null, onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(imageVector = icon, contentDescription = contentDescription, tint = MaterialTheme.colorScheme.onSurface)
    }
}
