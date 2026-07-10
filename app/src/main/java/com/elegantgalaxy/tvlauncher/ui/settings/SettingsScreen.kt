package com.elegantgalaxy.tvlauncher.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.elegantgalaxy.tvlauncher.BuildConfig
import com.elegantgalaxy.tvlauncher.ui.theme.AppBackground
import com.elegantgalaxy.tvlauncher.ui.theme.SurfaceElevated2
import com.elegantgalaxy.tvlauncher.utils.rememberTvFocusVisuals

private data class SettingsEntry(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
)

/**
 * Simple settings list. Each row is a placeholder for a real control
 * (volume slider, brightness slider, Wi-Fi picker) — wire those up as
 * follow-on tasks; this establishes the navigable list + focus pattern.
 */
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val entries = listOf(
        SettingsEntry(Icons.Filled.VolumeUp, "Volume", "Adjust system volume"),
        SettingsEntry(Icons.Filled.Brightness6, "Brightness", "Adjust screen brightness"),
        SettingsEntry(Icons.Filled.Wifi, "Network", "Wi-Fi and Ethernet settings"),
        SettingsEntry(Icons.Filled.Info, "About", "Version ${BuildConfig.VERSION_NAME} · Elegant Galaxy Pvt. Ltd."),
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
            items(entries) { entry ->
                SettingsRow(entry = entry, modifier = Modifier.padding(bottom = 12.dp))
            }
        }
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
                onClick = { /* wire up real setting screen per entry */ },
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
