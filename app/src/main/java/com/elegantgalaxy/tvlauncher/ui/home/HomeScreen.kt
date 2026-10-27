package com.elegantgalaxy.tvlauncher.ui.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import com.elegantgalaxy.tvlauncher.model.AppInfo
import com.elegantgalaxy.tvlauncher.ui.theme.AppBackground
import com.elegantgalaxy.tvlauncher.ui.theme.HomeContentEnd
import com.elegantgalaxy.tvlauncher.ui.theme.HomeContentStart
import com.elegantgalaxy.tvlauncher.utils.AppLauncherUtils
import com.elegantgalaxy.tvlauncher.utils.DisplayModeStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Launcher home screen: the normal hero/featured/category-carousel stack.
 * Search is a separate full-screen overlay ([SearchOverlay]) rather than
 * living here — see [com.elegantgalaxy.tvlauncher.navigation.TvLauncherNavGraph].
 * App data comes from [viewModel], which owns the PackageManager query so it
 * runs off the main thread and refreshes itself on install/uninstall.
 * [viewModel] is hoisted to [com.elegantgalaxy.tvlauncher.navigation.TvLauncherNavGraph]
 * and shared with [SearchOverlay] so its query state stays in sync.
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    DisplayModeStore.current(context) // ensures the persisted mode is loaded before first read below
    val displayMode by DisplayModeStore.mode.collectAsState()

    // Reactive background: whichever app tile currently has D-Pad focus
    // donates its icon's dominant color to a subtle tint at the bottom of
    // the screen, via Palette. Extraction runs off the main thread since
    // Palette.generate() is a blocking call; animateColorAsState smooths
    // the jump between colors as focus moves tile to tile.
    var targetAccent by remember { mutableStateOf(AppBackground) }
    LaunchedEffect(uiState.focusedApp) {
        val bitmap = uiState.focusedApp?.iconBitmap
        targetAccent = if (bitmap != null) {
            withContext(Dispatchers.Default) {
                val palette = Palette.from(bitmap).generate()
                val swatch = palette.dominantSwatch ?: palette.vibrantSwatch ?: palette.mutedSwatch
                swatch?.let { Color(it.rgb) } ?: AppBackground
            }
        } else {
            AppBackground
        }
    }
    val animatedAccent by animateColorAsState(
        targetValue = targetAccent,
        animationSpec = tween(700),
        label = "focusAccent",
    )

    val continueWatchingFocus = remember { FocusRequester() }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        // Translucent so the shared AppBackdrop (the Figma colours) stays true:
                        // Normal mode adds nothing at the top, and the focused-app tint is a
                        // whisper at the bottom rather than the old 25% wash that turned the
                        // page teal/green whenever a tile was focused.
                        listOf(
                            if (displayMode == DisplayMode.NORMAL) Color.Transparent else displayMode.tint.copy(alpha = 0.15f),
                            animatedAccent.copy(alpha = 0.07f),
                        ),
                    ),
                ),
        ) {
            // Gaps between sections come from the Figma frame (header-to-header pitch),
            // minus the focus-room padding each row already carries — hence per-item tops.
            LazyColumn(
                contentPadding = PaddingValues(bottom = 48.dp),
            ) {
                item {
                    HeroBanner(
                        banners = uiState.banners,
                        onBannerClick = { AppLauncherUtils.openBanner(context, it) },
                        modifier = Modifier.padding(start = HomeContentStart, end = HomeContentEnd),
                    )
                }

                item {
                    AppCarousel(
                        title = "Apps",
                        apps = uiState.allApps,
                        onAppClick = { app: AppInfo -> AppLauncherUtils.launch(context, app) },
                        onAppFocusChange = viewModel::onFocusedAppChange,
                        downFocusRequester = continueWatchingFocus,
                        modifier = Modifier.padding(top = 48.dp),
                    )
                }

                // Placeholder content (see MediaRow.kt) — click/See All are no-ops until a content source exists.
                item {
                    MediaRow(
                        title = "Continue Watching",
                        items = continueWatchingItems,
                        onItemClick = {},
                        onSeeAll = {},
                        rowFocusRequester = continueWatchingFocus,
                        modifier = Modifier.padding(top = 44.dp),
                    )
                }

                item {
                    MediaRow(
                        title = "Recommended",
                        items = recommendedItems,
                        onItemClick = {},
                        onSeeAll = {},
                        modifier = Modifier.padding(top = 31.dp),
                    )
                }
            }
        }
    }
}
