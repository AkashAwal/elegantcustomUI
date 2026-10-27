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
    Font(
        resId = R.font.plus_jakarta_sans,
        weight = FontWeight.Normal,
        variationSettings = FontVariation.Settings(FontVariation.weight(400)),
    ),
    Font(
        resId = R.font.plus_jakarta_sans,
        weight = FontWeight.Medium,
        variationSettings = FontVariation.Settings(FontVariation.weight(500)),
    ),
    Font(
        resId = R.font.plus_jakarta_sans,
        weight = FontWeight.SemiBold,
        variationSettings = FontVariation.Settings(FontVariation.weight(600)),
    ),
)

// Card titles (Figma: Plus Jakarta Sans, SemiBold, 28 / line height 36, -0.5px tracking).
val CardTitleStyle = TextStyle(
    fontFamily = PlusJakartaSans,
    fontWeight = FontWeight.SemiBold,
    fontSize = 28.sp,
    lineHeight = 36.sp,
    letterSpacing = (-0.5).sp,
)

// Body copy (Figma: Plus Jakarta Sans, Regular, 20 / line height 30, 0 tracking).
val BodyStyle = TextStyle(
    fontFamily = PlusJakartaSans,
    fontWeight = FontWeight.Normal,
    fontSize = 20.sp,
    lineHeight = 30.sp,
    letterSpacing = 0.sp,
)

// Navigation labels (Figma: Plus Jakarta Sans, Medium, 18 / line height 24, 0 tracking).
val NavigationStyle = TextStyle(
    fontFamily = PlusJakartaSans,
    fontWeight = FontWeight.Medium,
    fontSize = 18.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.sp,
)

// Captions (Figma: Plus Jakarta Sans, Regular, 14 / line height 20, +1px tracking).
val CaptionStyle = TextStyle(
    fontFamily = PlusJakartaSans,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = 1.sp,
)

// Carousel section titles (Figma: Plus Jakarta Sans, SemiBold, 36 / line height 44, -1px tracking).
val SectionTitleStyle = TextStyle(
    fontFamily = PlusJakartaSans,
    fontWeight = FontWeight.SemiBold,
    fontSize = 36.sp,
    lineHeight = 44.sp,
    letterSpacing = (-1).sp,
)

// Home screen row titles — Continue Watching, Recommended, apps (Figma "Section Header": Plus Jakarta Sans, SemiBold, 32).
val HomeSectionTitleStyle = TextStyle(
    fontFamily = PlusJakartaSans,
    fontWeight = FontWeight.SemiBold,
    fontSize = 32.sp,
)

// "See All ›" link beside a row title (Figma: Plus Jakarta Sans, Regular, 22).
val SeeAllStyle = TextStyle(
    fontFamily = PlusJakartaSans,
    fontWeight = FontWeight.Normal,
    fontSize = 22.sp,
)

// Home hero banner headline (Figma home frame: Plus Jakarta Sans, Bold, 48).
val HeroBannerTitleStyle = TextStyle(
    fontFamily = PlusJakartaSans,
    fontWeight = FontWeight.Bold,
    fontSize = 48.sp,
)

@OptIn(ExperimentalTextApi::class)
val Inter =FontFamily(
    Font(
        resId = R.font.inter,
        weight = FontWeight.Normal,
        variationSettings = FontVariation.Settings(FontVariation.weight(400)),
    ),
)

// Top bar clock (Figma: Inter, Regular, 32, line height auto, 0% tracking).
val TimeStyle = TextStyle(
    fontFamily = Inter,
    fontWeight = FontWeight.Normal,
    fontSize = 32.sp,
    letterSpacing = 0.sp,
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
