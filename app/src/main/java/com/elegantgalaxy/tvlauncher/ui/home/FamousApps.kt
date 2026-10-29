package com.elegantgalaxy.tvlauncher.ui.home

import androidx.compose.ui.graphics.Color

/**
 * Curated shortcuts to well-known streaming apps, shown in the "Trending
 * Apps" search row regardless of install status — unlike every other app
 * tile in this launcher, which always comes from a real PackageManager
 * query.
 *
 * Tile artwork, in order of preference:
 *  1. the device's real icon, if the package is installed (PackageManager read,
 *     same as every other icon in this launcher);
 *  2. the official logo, if one has been dropped into `res/drawable-nodpi/` as
 *     `ic_brand_<key>.png` (e.g. `ic_brand_netflix.png`). Brand assets must come from each
 *     company's own brand/press kit or partner portal, and follow its usage guidelines;
 *  3. a plain text badge in the brand's colour.
 * Tapping a tile launches the app if installed, otherwise opens its Play Store listing — see
 * [com.elegantgalaxy.tvlauncher.utils.AppLauncherUtils.launchOrOpenStore].
 */
data class FamousApp(
    val label: String,
    val key: String,
    val packageName: String,
    val color: Color,
    val description: String,
)

// Package names: Netflix, YouTube and Spotify are well known; Prime Video, JioHotstar and Airtel
// Xstream come from APKMirror's Android TV listings; Zee5 is unconfirmed. Verify all with
// `adb shell pm list packages` on the TV before shipping (a wrong id lands on a missing Play page).
val FamousAppsList = listOf(
    FamousApp("YouTube", "youtube", "com.google.android.youtube.tv", Color(0xFFFF0000), "Watch videos, music, live streams and trailers."),
    FamousApp("Netflix", "netflix", "com.netflix.ninja", Color(0xFFE50914), "Stream series, films and documentaries."),
    FamousApp("Prime Video", "primevideo", "com.amazon.amazonvideo.livingroom", Color(0xFF00A8E1), "Stream Prime originals, movies and shows."),
    FamousApp("JioHotstar", "jiohotstar", "in.startv.hotstar", Color(0xFF1F80E0), "Live sports, movies and shows in many languages."),
    FamousApp("Spotify", "spotify", "com.spotify.tv.android", Color(0xFF1DB954), "Listen to music, podcasts and playlists."),
    FamousApp("ZEE5", "zee5", "com.graymatrix.did", Color(0xFF8230C6), "Hindi and regional shows, movies and originals."),
    FamousApp("Airtel Xstream", "airtelxstream", "tv.airtel.xstream.tvapp", Color(0xFFE40000), "Movies, shows and live TV from Airtel."),
)
