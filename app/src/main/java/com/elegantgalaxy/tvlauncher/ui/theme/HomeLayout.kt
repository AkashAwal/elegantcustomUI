package com.elegantgalaxy.tvlauncher.ui.theme

import androidx.compose.ui.unit.dp

/**
 * Horizontal gutters of the home content area, from the Figma home frame
 * (content spans x=135..1840 of 1920, i.e. a 135 left / 80 right margin). The
 * left value is smaller than 135 because the 64dp side rail already takes the
 * first part of that margin — what's left is the gap between rail and content.
 * Top bar, hero, and every row share these so their edges line up.
 */
val HomeContentStart = 24.dp
val HomeContentEnd = 80.dp
