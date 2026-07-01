package com.elegantgalaxy.tvlauncher.navigation

/** Route constants for NavGraph. Kept as a sealed class instead of raw
 * strings so route typos become compile errors. */
sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Settings : Screen("settings")
}
