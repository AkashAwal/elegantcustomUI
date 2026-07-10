package com.elegantgalaxy.tvlauncher.ui.components

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
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
import com.elegantgalaxy.tvlauncher.ui.theme.BrandOnPrimary
import com.elegantgalaxy.tvlauncher.ui.theme.BrandPrimary
import com.elegantgalaxy.tvlauncher.ui.theme.SurfaceElevated1
import com.elegantgalaxy.tvlauncher.ui.theme.SurfaceElevated3
import com.elegantgalaxy.tvlauncher.utils.rememberTvFocusVisuals

/**
 * Rail entries backed by a real NavGraph route (see
 * [com.elegantgalaxy.tvlauncher.navigation.TvLauncherNavGraph]). SEARCH,
 * APPS, and GUIDE don't have dedicated screens yet — they currently just
 * navigate Home, since Home already hosts the search bar and app grid.
 * DISPLAY_SETTINGS, NETWORK_SETTINGS, and SETTINGS all currently point at
 * the same Settings screen (which already has Brightness/Network rows) —
 * split them into real dedicated screens later if they need to differ.
 */
enum class RailDestination(val icon: ImageVector, val contentDescription: String) {
    SEARCH(Icons.Filled.Search, "Search"),
    APPS(Icons.Filled.Apps, "Apps"),
    GUIDE(Icons.Filled.MenuBook, "Guide"),
    DISPLAY_SETTINGS(Icons.Filled.Brightness6, "Display settings"),
    NETWORK_SETTINGS(Icons.Filled.Wifi, "Network settings"),
    CONTACT(Icons.Filled.ContactPhone, "Contact"),
    SETTINGS(Icons.Filled.Settings, "Settings"),
}

/**
 * Persistent left navigation rail, docked alongside the NavHost content in
 * [com.elegantgalaxy.tvlauncher.navigation.TvLauncherNavGraph] rather than
 * living inside any one screen — this is what lets it stay visible while
 * Home/Settings swap in the content area next to it, PS5-dashboard style.
 *
 * Layout top to bottom: brand mark, divider, Search/Apps/Guide/Display
 * settings/Network settings, a second divider, then Contact/Settings.
 */
@Composable
fun SideNavRail(
    selected: RailDestination,
    onSelect: (RailDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Scrollable rather than relying on a fixed height budget: at common TV
    // resolutions/densities (e.g. 1920x1080 @ 320dpi = 540dp of height), a
    // logo + 2 dividers + 7 icons doesn't reliably fit un-scrolled, and a
    // clipped-but-invisible icon is worse than one you scroll to reach.
    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(96.dp)
            .background(SurfaceElevated1)
            .verticalScroll(rememberScrollState())
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BrandMark()

        Box(modifier = Modifier.padding(vertical = 20.dp)) {
            RailDivider()
        }

        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            listOf(
                RailDestination.SEARCH,
                RailDestination.APPS,
                RailDestination.GUIDE,
                RailDestination.DISPLAY_SETTINGS,
                RailDestination.NETWORK_SETTINGS,
            ).forEach { destination ->
                RailIcon(
                    icon = destination.icon,
                    contentDescription = destination.contentDescription,
                    isSelected = destination == selected,
                    onClick = { onSelect(destination) },
                )
            }
        }

        Box(modifier = Modifier.padding(vertical = 20.dp)) {
            RailDivider()
        }

        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            listOf(RailDestination.CONTACT, RailDestination.SETTINGS).forEach { destination ->
                RailIcon(
                    icon = destination.icon,
                    contentDescription = destination.contentDescription,
                    isSelected = destination == selected,
                    onClick = { onSelect(destination) },
                )
            }
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
        Image(
            painter = painterResource(id = R.drawable.company_logo),
            contentDescription = "Elegant Galaxy",
            colorFilter = ColorFilter.tint(BrandOnPrimary),
            modifier = Modifier.size(22.dp),
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
