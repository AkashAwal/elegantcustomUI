package com.elegantgalaxy.tvlauncher.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Cosmetic picture-mode presets for the launcher's own screens only. These
 * do NOT change actual TV color output — that lives in the SoC's video
 * pipeline via a vendor-specific API this launcher doesn't have access to
 * (see project notes). Each mode is just a background tint blended into
 * the existing gradient, so it's cheap (no extra composition layer/filter).
 */
enum class DisplayMode(val label: String, val tint: Color) {
    NORMAL("Normal", AppBackground),
    VIVID("Vivid", Color(0xFF001A33)),
    WARM("Warm", Color(0xFF2A1505)),
    CINEMA("Cinema", Color(0xFF000000)),
    GAME("Game", Color(0xFF001A1A)),
}
