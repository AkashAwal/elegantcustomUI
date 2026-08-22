package com.elegantgalaxy.tvlauncher.ui.home

import androidx.compose.ui.graphics.Color

/**
 * Curated shortcuts to well-known streaming apps, shown in search regardless
 * of install status — unlike every other app tile in this launcher, which
 * always comes from a real PackageManager query. No brand icon assets are
 * bundled here (avoiding reproducing trademarked logos), so each shortcut
 * is a plain text badge in the brand's color instead. Tapping one launches
 * it if installed, otherwise opens its Play Store listing — see
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
    FamousApp("Spotify", "com.spotify.tv.android", Color(0xFF1DB954)),
)
