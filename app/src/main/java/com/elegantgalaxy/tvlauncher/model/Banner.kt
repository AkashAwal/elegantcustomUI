package com.elegantgalaxy.tvlauncher.model

/**
 * One hero-banner slide. [imagePath] is a downsized local file written by
 * [com.elegantgalaxy.tvlauncher.utils.BannerRepository] (null = gradient only).
 * Clicking opens [deepLink] inside [packageName]; see
 * [com.elegantgalaxy.tvlauncher.utils.AppLauncherUtils.openBanner] for fallbacks.
 */
data class Banner(
    val id: String,
    val title: String,
    val subtitle: String,
    val accentArgb: Int,
    val imagePath: String?,
    val packageName: String,
    val deepLink: String?,
    /** Optional dot-separated facts line under the title, e.g. "IMDb 8.8 · 2018 · 4 Seasons"; empty = hidden. */
    val meta: String = "",
)
