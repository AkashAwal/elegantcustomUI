package com.elegantgalaxy.tvlauncher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.elegantgalaxy.tvlauncher.ui.theme.SurfaceElevated1
import com.elegantgalaxy.tvlauncher.utils.rememberTvFocusVisuals

enum class RailDestination(val icon: ImageVector, val contentDescription: String) {
    HOME(Icons.Filled.Home, "Home"),
    APPS(Icons.Filled.Apps, "All apps"),
    SETTINGS(Icons.Filled.Settings, "Settings"),
}

/**
 * Persistent left navigation rail, docked alongside the NavHost content in
 * [com.elegantgalaxy.tvlauncher.navigation.TvLauncherNavGraph] rather than
 * living inside any one screen — this is what lets it stay visible while
 * Home/Settings swap in the content area next to it, PS5-dashboard style.
 */
@Composable
fun SideNavRail(
    selected: RailDestination,
    onSelect: (RailDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(96.dp)
            .background(SurfaceElevated1)
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        RailDestination.entries.forEach { destination ->
            RailIcon(
                destination = destination,
                isSelected = destination == selected,
                onClick = { onSelect(destination) },
            )
        }
    }
}

@Composable
private fun RailIcon(
    destination: RailDestination,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(16.dp)
    val focusVisuals = rememberTvFocusVisuals(shape = shape)

    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
    } else {
        SurfaceElevated1
    }
    val tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant

    Icon(
        imageVector = destination.icon,
        contentDescription = destination.contentDescription,
        tint = tint,
        modifier = Modifier
            .then(focusVisuals.modifier)
            .clip(shape)
            .background(backgroundColor)
            .clickable(
                interactionSource = focusVisuals.interactionSource,
                indication = null,
                onClick = onClick,
            )
            .padding(16.dp),
    )
}
