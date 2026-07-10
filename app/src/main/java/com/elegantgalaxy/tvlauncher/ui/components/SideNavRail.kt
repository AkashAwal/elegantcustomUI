package com.elegantgalaxy.tvlauncher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
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
import com.elegantgalaxy.tvlauncher.ui.theme.BrandPrimary
import com.elegantgalaxy.tvlauncher.ui.theme.BrandOnPrimary
import com.elegantgalaxy.tvlauncher.ui.theme.SurfaceElevated1
import com.elegantgalaxy.tvlauncher.ui.theme.SurfaceElevated3
import com.elegantgalaxy.tvlauncher.utils.rememberTvFocusVisuals

/**
 * Rail entries backed by a real NavGraph route (see
 * [com.elegantgalaxy.tvlauncher.navigation.TvLauncherNavGraph]). SEARCH,
 * APPS, and GUIDE don't have dedicated screens yet — they currently just
 * navigate Home — since Home already hosts the search bar and app grid.
 * Wire them to real destinations once those screens exist.
 */
enum class RailDestination(val icon: ImageVector, val contentDescription: String) {
    SEARCH(Icons.Filled.Search, "Search"),
    APPS(Icons.Filled.Apps, "Apps"),
    GUIDE(Icons.Filled.LiveTv, "Guide"),
    HOME(Icons.Filled.Home, "Home"),
    SETTINGS(Icons.Filled.Settings, "Settings"),
}

/**
 * Persistent left navigation rail, docked alongside the NavHost content in
 * [com.elegantgalaxy.tvlauncher.navigation.TvLauncherNavGraph] rather than
 * living inside any one screen — this is what lets it stay visible while
 * Home/Settings swap in the content area next to it, PS5-dashboard style.
 *
 * Layout top to bottom: brand mark, divider, the five [RailDestination]
 * icons, a second divider, then Contact/Notifications — the latter two are
 * presentation-only placeholders for now (see [onContactClick] /
 * [onNotificationsClick]); there's no Contact or Notifications screen in
 * this app yet.
 */
@Composable
fun SideNavRail(
    selected: RailDestination,
    onSelect: (RailDestination) -> Unit,
    onContactClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(96.dp)
            .background(SurfaceElevated1)
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BrandMark()

        Box(modifier = Modifier.padding(vertical = 20.dp)) {
            RailDivider()
        }

        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            RailDestination.entries.forEach { destination ->
                RailIcon(
                    icon = destination.icon,
                    contentDescription = destination.contentDescription,
                    isSelected = destination == selected,
                    onClick = { onSelect(destination) },
                )
            }
        }

        Box(modifier = Modifier.weight(1f))

        Box(modifier = Modifier.padding(bottom = 20.dp)) {
            RailDivider()
        }

        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            RailIcon(
                icon = Icons.Filled.Call,
                contentDescription = "Contact",
                isSelected = false,
                onClick = onContactClick,
            )
            RailIcon(
                icon = Icons.Filled.Notifications,
                contentDescription = "Notifications",
                isSelected = false,
                onClick = onNotificationsClick,
            )
        }
    }
}

@Composable
private fun BrandMark() {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(BrandPrimary),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "E",
            color = BrandOnPrimary,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
private fun RailDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(1.dp)
            .background(SurfaceElevated3),
    )
}

@Composable
private fun RailIcon(
    icon: ImageVector,
    contentDescription: String,
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
        imageVector = icon,
        contentDescription = contentDescription,
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
