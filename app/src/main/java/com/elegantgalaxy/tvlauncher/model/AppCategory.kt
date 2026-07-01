package com.elegantgalaxy.tvlauncher.model

/**
 * Groups apps into carousel rows on the home screen. [displayName] is
 * shown as the row header; add new categories here as the app catalogue
 * grows (e.g. GAMES, MUSIC).
 */
enum class AppCategory(val displayName: String) {
    STREAMING("Streaming"),
    TOOLS("Tools"),
    SETTINGS("Settings"),
    OTHER("Other"),
}
