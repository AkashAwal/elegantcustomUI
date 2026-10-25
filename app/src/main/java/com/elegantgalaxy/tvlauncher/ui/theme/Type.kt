package com.elegantgalaxy.tvlauncher.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.elegantgalaxy.tvlauncher.R

// Plus Jakarta Sans ships as a single variable font; minSdk 28 supports
// variation settings, so one file covers every weight we ask for.
@OptIn(ExperimentalTextApi::class)
val PlusJakartaSans = FontFamily(
    Font(
        resId = R.font.plus_jakarta_sans,
        weight = FontWeight.Bold,
        variationSettings = FontVariation.Settings(FontVariation.weight(700)),
    ),
)

// Hero banner title (Figma: Plus Jakarta Sans, Bold, 64 / line height 72, -2.5px tracking).
val HeroTitleStyle = TextStyle(
    fontFamily = PlusJakartaSans,
    fontWeight = FontWeight.Bold,
    fontSize = 64.sp,
    lineHeight = 72.sp,
    letterSpacing = (-2.5).sp,
)

// Font sizes are intentionally larger than phone defaults — TVs are
// viewed from a couch, several meters away ("10-foot UI" convention).
val TvTypography = Typography(
    displayLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 57.sp),
    headlineLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 32.sp),
    headlineMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 28.sp),
    titleLarge = TextStyle(fontWeight = FontWeight.Medium, fontSize = 24.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 20.sp),
    bodyLarge = TextStyle(fontWeight = FontWeight.Normal, fontSize = 18.sp),
    bodyMedium = TextStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.Medium, fontSize = 16.sp),
    labelMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp),
)
