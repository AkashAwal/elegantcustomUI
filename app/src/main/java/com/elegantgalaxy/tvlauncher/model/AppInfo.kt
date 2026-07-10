package com.elegantgalaxy.tvlauncher.model

import android.graphics.Bitmap
import androidx.compose.ui.graphics.painter.Painter

/**
 * A single launchable app tile shown in the grid/carousel.
 *
 * [packageName] is what actually gets launched (via PackageManager); the
 * rest is presentation data. [icon] is nullable because it is resolved
 * lazily from PackageManager and may fail for a stale/uninstalled entry.
 * [iconBitmap] is the same icon as a raw [Bitmap] (rather than the [Painter]
 * Compose draws) so the focus-reactive background can run it through
 * Palette for a dominant color without re-decoding anything.
 */
data class AppInfo(
    val packageName: String,
    val activityName: String,
    val label: String,
    val category: AppCategory,
    val icon: Painter? = null,
    val iconBitmap: Bitmap? = null,
    val isSystemApp: Boolean = false,
)
