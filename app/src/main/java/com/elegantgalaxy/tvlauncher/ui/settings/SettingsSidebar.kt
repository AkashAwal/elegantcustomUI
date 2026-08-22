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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
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
    var wifiStatus by remember { mutableStateOf(SettingsActions.getWifiStatusLabel(context)) }

    // Refreshes when the app comes back to the foreground (e.g. Back from
    // the system Wi-Fi settings screen this row deep-links to), since the
    // Activity isn't destroyed/recreated on that round trip.
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                wifiStatus = SettingsActions.getWifiStatusLabel(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(300.dp)
            .background(SurfaceElevated1)
            .padding(16.dp),
    ) {
        FullWidthRow(
            label = "Wi-Fi Settings",
            subtitle = wifiStatus,
            icon = Icons.Filled.Wifi,
            onClick = { SettingsActions.openNetworkSettings(context) },
        )

        Spacer(Modifier.height(14.dp))

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
                useStepper = true,
            ) { index ->
                val clamped = index.coerceIn(0, SettingsActions.brightnessLevels.lastIndex)
                SettingsActions.setBrightnessPercent(context, SettingsActions.brightnessLevels[clamped])
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                pair.forEach { tile ->
                    QuickSettingTile(tile = tile, modifier = Modifier.weight(1f).fillMaxHeight())
                }
            }
            Spacer(Modifier.height(14.dp))
        }

        Spacer(Modifier.weight(1f))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
                .height(1.dp)
                .background(SurfaceElevated3),
        )

        FullWidthRow(label = "Support", icon = Icons.Filled.SupportAgent, onClick = {}, verticalPadding = 12.dp)

        Spacer(Modifier.height(8.dp))

        FullWidthRow(label = "Contact Us", icon = Icons.Filled.ContactPhone, onClick = {}, verticalPadding = 12.dp)
    }
}

private fun nearestIndex(levels: List<Int>, value: Int): Int =
    levels.indices.minByOrNull { kotlin.math.abs(levels[it] - value) } ?: 0

private class TileSpec<T>(
    val label: String,
    val options: List<T>,
    val selectedIndex: Int,
    val valueLabel: (T) -> String,
    val useStepper: Boolean = false,
    val onSelect: (Int) -> Unit,
)

@Composable
private fun FullWidthRow(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    verticalPadding: Dp = 16.dp,
    subtitle: String? = null,
) {
    val shape = RoundedCornerShape(10.dp)
    val focusVisuals = rememberTvFocusVisuals(shape = shape)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .then(focusVisuals.modifier)
            .clip(shape)
            .background(SurfaceElevated2)
            .clickable(interactionSource = focusVisuals.interactionSource, indication = null, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = verticalPadding),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp),
                )
            }
        }
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
            .padding(vertical = 13.dp, horizontal = 14.dp),
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
                .padding(top = 8.dp),
        )
        if (tile.useStepper) {
            Row(
                modifier = Modifier.padding(top = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                StepButton(icon = Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Decrease ${tile.label}") {
                    tile.onSelect(tile.selectedIndex - 1)
                }
                StepButton(icon = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Increase ${tile.label}") {
                    tile.onSelect(tile.selectedIndex + 1)
                }
            }
        } else {
            DotSelector(
                count = tile.options.size,
                selectedIndex = tile.selectedIndex,
                onSelect = tile.onSelect,
                modifier = Modifier.padding(top = 12.dp),
            )
        }
    }
}

@Composable
private fun StepButton(icon: ImageVector, contentDescription: String, onClick: () -> Unit) {
    val shape = RoundedCornerShape(8.dp)
    val focusVisuals = rememberTvFocusVisuals(shape = shape)

    Box(
        modifier = Modifier
            .size(28.dp)
            .then(focusVisuals.modifier)
            .clip(shape)
            .background(SurfaceElevated3)
            .clickable(interactionSource = focusVisuals.interactionSource, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(imageVector = icon, contentDescription = contentDescription, tint = MaterialTheme.colorScheme.onSurface)
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
