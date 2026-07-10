package com.elegantgalaxy.tvlauncher.ui.theme

import androidx.compose.ui.graphics.Color

// --- Elegant Galaxy brand palette -------------------------------------
// PS5-dashboard-inspired: near-black with a faint blue undertone, one
// electric-blue accent. Swap these hex values once real brand guidelines
// are supplied — every screen pulls from here, nothing is hardcoded
// per-composable.
val BrandPrimary = Color(0xFF00A3FF)      // electric blue accent (buttons, focus ring)
val BrandSecondary = Color(0xFF7C5CFF)    // violet secondary accent (highlights, badges)
val BrandOnPrimary = Color(0xFFFFFFFF)

// --- OLED-friendly dark surfaces ---------------------------------------
// True black with a faint blue tint (not neutral grey) so OLED pixels
// mostly switch off while still reading as "PS5 dashboard" rather than
// flat black. Saves power and improves contrast on burn-in-prone panels.
val SurfaceBlack = Color(0xFF05030A)
val SurfaceElevated1 = Color(0xFF10121A)
val SurfaceElevated2 = Color(0xFF1A1D28)
val SurfaceElevated3 = Color(0xFF262A38)

// Black-to-gold background gradient. ~90% opacity (not fully opaque) so the
// home screen's golden particle canvas can still show through faintly
// underneath it; fully opaque on screens with no particle layer (Settings)
// is visually indistinguishable since there's nothing behind it anyway.
val GradientBlack = Color(0xE605030A)
val GradientGold = Color(0xE66B4F23)

val TextPrimary = Color(0xFFF5F5F5)
val TextSecondary = Color(0xFFB3B3B3)
val TextDisabled = Color(0xFF6B6B6B)

// Focus ring color used across D-Pad-navigable components. Kept distinct
// from BrandPrimary so it stays visible on tiles that already use the
// brand color as their background.
val FocusRing = Color(0xFFFFFFFF)

val ErrorColor = Color(0xFFCF6679)
