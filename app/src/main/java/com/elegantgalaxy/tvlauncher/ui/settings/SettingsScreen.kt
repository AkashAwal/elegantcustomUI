package com.elegantgalaxy.tvlauncher.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.elegantgalaxy.tvlauncher.BuildConfig
import com.elegantgalaxy.tvlauncher.ui.theme.AppBackground
import com.elegantgalaxy.tvlauncher.ui.theme.DisplayMode
import com.elegantgalaxy.tvlauncher.ui.theme.SurfaceElevated2
import com.elegantgalaxy.tvlauncher.ui.theme.SurfaceElevated3
import com.elegantgalaxy.tvlauncher.utils.DisplayModeStore
import com.elegantgalaxy.tvlauncher.utils.SettingsActions
import com.elegantgalaxy.tvlauncher.utils.rememberTvFocusVisuals

private data class SettingsEntry(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val onClick: () -> Unit,
)

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var selectedMode by remember { mutableStateOf(DisplayModeStore.current(context)) }

    val entries = listOf(
        SettingsEntry(
            Icons.AutoMirrored.Filled.VolumeUp,
            "Volume",
            "Adjust system volume",
            onClick = { SettingsActions.adjustVolume(context) },
        ),
        SettingsEntry(
            Icons.Filled.Wifi,
            "Network",
            "Wi-Fi and Ethernet settings",
            onClick = { SettingsActions.openNetworkSettings(context) },
        ),
        SettingsEntry(
            Icons.Filled.Info,
            "About",
            "Version ${BuildConfig.VERSION_NAME} · Elegant Galaxy Pvt. Ltd.",
            onClick = {},
        ),
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(horizontal = 48.dp, vertical = 24.dp),
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
        )

        LazyColumn(
            contentPadding = PaddingValues(top = 24.dp),
        ) {
            item {
                DisplayModeSection(
                    selected = selectedMode,
                    onSelect = { mode ->
                        selectedMode = mode
                        DisplayModeStore.setMode(context, mode)
                    },
                    modifier = Modifier.padding(bottom = 12.dp),
                )
            }

            item {
                BrightnessRow(modifier = Modifier.padding(bottom = 12.dp))
            }

            items(entries) { entry ->
                SettingsRow(entry = entry, modifier = Modifier.padding(bottom = 12.dp))
            }
        }
    }
}

/**
 * Picture-mode picker. These tint the launcher's own background only — see
 * [DisplayMode] doc — so the subtitle says so explicitly rather than
 * implying it calibrates the actual TV picture.
 */
@Composable
private fun DisplayModeSection(
    selected: DisplayMode,
    onSelect: (DisplayMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Display Mode",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = "Changes launcher appearance only, not TV picture output",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        LazyRow(
            contentPadding = PaddingValues(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(DisplayMode.entries) { mode ->
                DisplayModeChip(mode = mode, isSelected = mode == selected, onClick = { onSelect(mode) })
            }
        }
    }
}

@Composable
private fun DisplayModeChip(mode: DisplayMode, isSelected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(10.dp)
    val focusVisuals = rememberTvFocusVisuals(shape = shape)
    val background = if (isSelected) MaterialTheme.colorScheme.primary else SurfaceElevated2

    Row(
        modifier = Modifier
            .then(focusVisuals.modifier)
            .clip(shape)
            .background(background)
            .clickable(
                interactionSource = focusVisuals.interactionSource,
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = mode.label,
            style = MaterialTheme.typography.titleSmall,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun BrightnessRow(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var percent by remember { mutableIntStateOf(SettingsActions.getBrightnessPercent(context)) }
    val shape = RoundedCornerShape(10.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(SurfaceElevated2)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Filled.Brightness6,
            contentDescription = "Brightness",
            tint = MaterialTheme.colorScheme.onSurface,
        )
        Column(modifier = Modifier.padding(start = 20.dp).weight(1f)) {
            Text(
                text = "Brightness",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "$percent%",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        StepButton(
            icon = Icons.Filled.Remove,
            contentDescription = "Decrease brightness",
            onClick = {
                SettingsActions.decreaseBrightness(context)
                percent = SettingsActions.getBrightnessPercent(context)
            },
        )
        StepButton(
            icon = Icons.Filled.Add,
            contentDescription = "Increase brightness",
            onClick = {
                SettingsActions.increaseBrightness(context)
                percent = SettingsActions.getBrightnessPercent(context)
            },
            modifier = Modifier.padding(start = 12.dp),
        )
    }
}

@Composable
private fun StepButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(8.dp)
    val focusVisuals = rememberTvFocusVisuals(shape = shape)

    Row(
        modifier = modifier
            .size(44.dp)
            .then(focusVisuals.modifier)
            .clip(shape)
            .background(SurfaceElevated3)
            .clickable(
                interactionSource = focusVisuals.interactionSource,
                indication = null,
                onClick = onClick,
            ),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(imageVector = icon, contentDescription = contentDescription, tint = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun SettingsRow(entry: SettingsEntry, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(10.dp)
    val focusVisuals = rememberTvFocusVisuals(shape = shape)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(focusVisuals.modifier)
            .clip(shape)
            .background(SurfaceElevated2)
            .clickable(
                interactionSource = focusVisuals.interactionSource,
                indication = null,
                onClick = entry.onClick,
            )
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = entry.icon,
            contentDescription = entry.title,
            tint = MaterialTheme.colorScheme.onSurface,
        )
        Column(modifier = Modifier.padding(start = 20.dp)) {
            Text(
                text = entry.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = entry.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
