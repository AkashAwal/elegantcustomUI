package com.elegantgalaxy.tvlauncher.utils

import com.elegantgalaxy.tvlauncher.model.AppCategory
import com.elegantgalaxy.tvlauncher.model.AppInfo

/**
 * Placeholder catalogue so the UI can be built and previewed before
 * PackageManager integration (AppLauncherUtils.queryLaunchableApps) is
 * wired up on real hardware. Icons are left null; AppTile falls back to
 * a letter avatar when icon == null.
 */
object MockData {
    val sampleApps: List<AppInfo> = listOf(
        AppInfo("com.google.android.youtube.tv", "MainActivity", "YouTube", AppCategory.STREAMING),
        AppInfo("com.netflix.ninja", "MainActivity", "Netflix", AppCategory.STREAMING),
        AppInfo("in.startv.hotstar", "MainActivity", "Hotstar", AppCategory.STREAMING),
        AppInfo("com.amazon.avod.thirdpartyclient", "MainActivity", "Prime Video", AppCategory.STREAMING),
        AppInfo("com.spotify.tv.android", "MainActivity", "Spotify", AppCategory.STREAMING),
        AppInfo("com.android.tv.settings", "MainActivity", "Device Settings", AppCategory.TOOLS),
        AppInfo("com.google.android.tv.files", "MainActivity", "File Manager", AppCategory.TOOLS),
        AppInfo("com.android.chrome", "MainActivity", "Browser", AppCategory.TOOLS),
        AppInfo("com.elegantgalaxy.diagnostics", "MainActivity", "TV Diagnostics", AppCategory.OTHER),
    )
}
