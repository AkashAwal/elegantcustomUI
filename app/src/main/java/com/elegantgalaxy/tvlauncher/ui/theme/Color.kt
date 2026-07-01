package com.elegantgalaxy.tvlauncher.ui.theme

import androidx.compose.ui.graphics.Color

// --- Elegant Galaxy brand palette -------------------------------------
// Swap these hex values once the real brand guidelines / logo colors
// are supplied — every screen pulls from here, nothing is hardcoded
// per-composable.
val BrandPrimary = Color(0xFF6C4CFF)      // primary accent (buttons, focus ring)
val BrandSecondary = Color(0xFF00D1B2)    // secondary accent (highlights, badges)
val BrandOnPrimary = Color(0xFFFFFFFF)

// --- OLED-friendly dark surfaces ---------------------------------------
// True black (not dark grey) so OLED pixels fully switch off on TVs that
// support it, saving power and improving contrast on burn-in-prone panels.
val SurfaceBlack = Color(0xFF000000)
val SurfaceElevated1 = Color(0xFF121212)
val SurfaceElevated2 = Color(0xFF1E1E1E)
val SurfaceElevated3 = Color(0xFF2A2A2A)

val TextPrimary = Color(0xFFF5F5F5)
val TextSecondary = Color(0xFFB3B3B3)
val TextDisabled = Color(0xFF6B6B6B)

// Focus ring color used across D-Pad-navigable components. Kept distinct
// from BrandPrimary so it stays visible on tiles that already use the
// brand color as their background.
val FocusRing = Color(0xFFFFFFFF)

val ErrorColor = Color(0xFFCF6679)
