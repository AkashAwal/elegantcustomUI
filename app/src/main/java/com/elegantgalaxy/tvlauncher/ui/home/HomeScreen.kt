package com.elegantgalaxy.tvlauncher.ui.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.palette.graphics.Palette
import com.elegantgalaxy.tvlauncher.model.AppCategory
import com.elegantgalaxy.tvlauncher.model.AppInfo
import com.elegantgalaxy.tvlauncher.ui.components.GoldenParticleBackground
import com.elegantgalaxy.tvlauncher.ui.components.SearchBar
import com.elegantgalaxy.tvlauncher.ui.theme.AppBackground
import com.elegantgalaxy.tvlauncher.utils.AppLauncherUtils
import com.elegantgalaxy.tvlauncher.utils.DisplayModeStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Launcher home screen: search bar, then either the normal hero/featured/
 * category-carousel stack, or (while a search query is active) a flat grid
 * of matching apps. App data comes from [HomeViewModel], which owns the
 * PackageManager query so it runs off the main thread and refreshes itself
 * on install/uninstall.
 */
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val viewModel: HomeViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

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

    Box(modifier = modifier.fillMaxSize()) {
        GoldenParticleBackground(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(AppBackground.copy(alpha = 0.92f), animatedAccent.copy(alpha = 0.55f)),
                    ),
                ),
        ) {
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = viewModel::onSearchQueryChange,
                onSearchSubmit = { AppLauncherUtils.launchYouTubeSearch(context, uiState.searchQuery) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp, vertical = 16.dp),
            )

            if (uiState.searchQuery.isBlank()) {
                val appsByCategory = uiState.appsByCategory
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(32.dp),
                    contentPadding = PaddingValues(bottom = 16.dp),
                ) {
                    item { HeroBanner() }

                    item {
                        FeaturedContentRow(
                            onAndroidTvClick = {},
                            onInputsClick = {},
                        )
                    }

                    items(AppCategory.entries.filter { appsByCategory[it]?.isNotEmpty() == true }) { category ->
                        AppCarousel(
                            title = category.displayName,
                            apps = appsByCategory[category].orEmpty(),
                            onAppClick = { app: AppInfo -> AppLauncherUtils.launch(context, app) },
                            onAppFocusChange = viewModel::onFocusedAppChange,
                        )
                    }
                }
            } else {
                AppGrid(
                    apps = uiState.searchResults,
                    onAppClick = { app -> AppLauncherUtils.launch(context, app) },
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}
