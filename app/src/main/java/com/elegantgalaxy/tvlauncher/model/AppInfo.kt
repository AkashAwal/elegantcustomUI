package com.elegantgalaxy.tvlauncher.model

import androidx.compose.ui.graphics.painter.Painter

/**
 * A single launchable app tile shown in the grid/carousel.
 *
 * [packageName] is what actually gets launched (via PackageManager); the
 * rest is presentation data. [icon] is nullable because it is resolved
 * lazily from PackageManager and may fail for a stale/uninstalled entry.
 */
data class AppInfo(
    val packageName: String,
    val activityName: String,
    val label: String,
    val category: AppCategory,
    val icon: Painter? = null,
    val isSystemApp: Boolean = false,
)
