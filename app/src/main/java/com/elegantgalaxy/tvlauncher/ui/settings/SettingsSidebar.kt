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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
 * Every middle item is an inline dot selector — no chevrons, no drill-down screens.
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

        val eyeCareOptions = listOf(false, true)
        val tiles: List<TileSpec<*>> = listOf(
            TileSpec("Picture Mode", DisplayMode.entries, DisplayMode.entries.indexOf(displayMode), { it.label }) { index ->
                DisplayModeStore.setMode(context, DisplayMode.entries[index])
            },
            TileSpec(
                "Brightness",
                SettingsActions.brightnessLevels,
                nearestIndex(SettingsActions.brightnessLevels, brightnessPercent),
                { "$it%" },
            ) { index ->
                SettingsActions.setBrightnessPercent(context, SettingsActions.brightnessLevels[index])
                brightnessPercent = SettingsActions.getBrightnessPercent(context)
            },
            TileSpec("Sound Mode", SoundMode.entries, SoundMode.entries.indexOf(soundMode), { it.label }) { index ->
                QuickSettingsStore.setSoundMode(context, SoundMode.entries[index])
            },
            TileSpec("Sound Output", SoundOutput.entries, SoundOutput.entries.indexOf(soundOutput), { it.label }) { index ->
                QuickSettingsStore.setSoundOutput(context, SoundOutput.entries[index])
            },
            TileSpec("Sleep Timer", SleepTimer.entries, SleepTimer.entries.indexOf(sleepTimer), { it.label }) { index ->
                QuickSettingsStore.setSleepTimer(context, SleepTimer.entries[index])
            },
            TileSpec("Eye Care Mode", eyeCareOptions, eyeCareOptions.indexOf(eyeCareEnabled), { if (it) "On" else "Off" }) { index ->
                QuickSettingsStore.setEyeCareEnabled(context, eyeCareOptions[index])
            },
        )

        tiles.chunked(2).forEach { pair ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                pair.forEach { tile ->
                    QuickSettingTile(tile = tile, modifier = Modifier.weight(1f))
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        Spacer(Modifier.weight(1f))

        FullWidthRow(label = "Contact Us", icon = Icons.Filled.ContactPhone, onClick = {})
    }
}

private fun nearestIndex(levels: List<Int>, value: Int): Int =
    levels.indices.minByOrNull { kotlin.math.abs(levels[it] - value) } ?: 0

private class TileSpec<T>(
    val label: String,
    val options: List<T>,
    val selectedIndex: Int,
    val valueLabel: (T) -> String,
    val onSelect: (Int) -> Unit,
)

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
private fun <T> QuickSettingTile(tile: TileSpec<T>, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(10.dp)

    Column(
        modifier = modifier
            .clip(shape)
            .background(SurfaceElevated2)
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = tile.label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            maxLines = 1,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = tile.valueLabel(tile.options[tile.selectedIndex]),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
        )
        DotSelector(
            count = tile.options.size,
            selectedIndex = tile.selectedIndex,
            onSelect = tile.onSelect,
            modifier = Modifier.padding(top = 10.dp),
        )
    }
}

/** All dots the same size — the active option is just white, no enlargement. */
@Composable
private fun DotSelector(count: Int, selectedIndex: Int, onSelect: (Int) -> Unit, modifier: Modifier = Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(count) { index ->
            val isSelected = index == selectedIndex
            val focusVisuals = rememberTvFocusVisuals(shape = CircleShape)
            Box(
                modifier = Modifier
                    .then(focusVisuals.modifier)
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) Color.White else SurfaceElevated3)
                    .clickable(
                        interactionSource = focusVisuals.interactionSource,
                        indication = null,
                        onClick = { onSelect(index) },
                    ),
            )
        }
    }
}
