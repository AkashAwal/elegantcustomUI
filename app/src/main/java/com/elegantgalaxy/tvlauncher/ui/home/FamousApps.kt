package com.elegantgalaxy.tvlauncher.ui.home

import androidx.compose.ui.graphics.Color

/**
 * Curated shortcuts to well-known streaming apps, shown in the "Trending
 * Apps" search row regardless of install status — unlike every other app
 * tile in this launcher, which always comes from a real PackageManager
 * query. Exactly 5, sized to fit one row without scrolling.
 *
 * No brand logo image assets are bundled here (that would mean reproducing
 * trademarked logos without a license — a real risk for a shipped
 * commercial product). Instead: if one of these packages is actually
 * installed, its tile shows the device's real icon (a legitimate
 * PackageManager read, same as every other icon in this launcher); if not
 * installed, it falls back to a plain text badge in the brand's color. See
 * [com.elegantgalaxy.tvlauncher.utils.AppLauncherUtils.getInstalledAppIcon].
 * Tapping a tile launches the app if installed, otherwise opens its Play
 * Store listing — see
 * [com.elegantgalaxy.tvlauncher.utils.AppLauncherUtils.launchOrOpenStore].
 */
data class FamousApp(
    val label: String,
    val packageName: String,
    val color: Color,
)

val FamousAppsList = listOf(
    FamousApp("Netflix", "com.netflix.ninja", Color(0xFFE50914)),
    FamousApp("Prime Video", "com.amazon.avod.thirdpartyclient", Color(0xFF00A8E1)),
    FamousApp("Disney+", "com.disney.disneyplus", Color(0xFF113CCF)),
    FamousApp("YouTube", "com.google.android.youtube.tv", Color(0xFFFF0000)),
    FamousApp("Hotstar", "in.startv.hotstar", Color(0xFF1F80E0)),
)
