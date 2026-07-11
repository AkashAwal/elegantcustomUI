package com.elegantgalaxy.tvlauncher.ui.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import com.elegantgalaxy.tvlauncher.R
import com.elegantgalaxy.tvlauncher.model.AppCategory
import com.elegantgalaxy.tvlauncher.model.AppInfo
import com.elegantgalaxy.tvlauncher.ui.components.AppTile
import com.elegantgalaxy.tvlauncher.ui.components.GoldenParticleBackground
import com.elegantgalaxy.tvlauncher.ui.components.SearchBar
import com.elegantgalaxy.tvlauncher.ui.theme.AppBackground
import com.elegantgalaxy.tvlauncher.utils.AppLauncherUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Launcher home screen: branding header, then one horizontally-scrolling
 * [AppCarousel] per [AppCategory] that has apps, populated from the real
 * installed-app list via [AppLauncherUtils.queryLaunchableApps].
 */
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val apps = remember { AppLauncherUtils.queryLaunchableApps(context) }
    val appsByCategory = remember(apps) { apps.groupBy { it.category } }

    var query by remember { mutableStateOf("") }
    val filteredApps = remember(apps, query) {
        if (query.isBlank()) emptyList() else apps.filter { it.label.contains(query, ignoreCase = true) }
    }

    // Reactive background: whichever app tile currently has D-Pad focus
    // donates its icon's dominant color to a subtle tint at the bottom of
    // the screen, via Palette. Extraction runs off the main thread since
    // Palette.generate() is a blocking call; animateColorAsState smooths
    // the jump between colors as focus moves tile to tile.
    var focusedApp by remember { mutableStateOf<AppInfo?>(null) }
    var targetAccent by remember { mutableStateOf(AppBackground) }
    LaunchedEffect(focusedApp) {
        val bitmap = focusedApp?.iconBitmap
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
            HomeHeader(
                query = query,
                onQueryChange = { query = it },
                onSearchSubmit = { AppLauncherUtils.launchYouTubeSearch(context, query) },
            )

            if (query.isNotBlank()) {
                SearchResultsRow(
                    apps = filteredApps,
                    onAppClick = { app -> AppLauncherUtils.launch(context, app) },
                    onAppFocusChange = { focusedApp = it },
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(32.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 8.dp, bottom = 16.dp),
                ) {
                    item { HeroBanner() }

                    items(AppCategory.entries.filter { appsByCategory[it]?.isNotEmpty() == true }) { category ->
                        AppCarousel(
                            title = category.displayName,
                            apps = appsByCategory[category].orEmpty(),
                            onAppClick = { app: AppInfo -> AppLauncherUtils.launch(context, app) },
                            onAppFocusChange = { focusedApp = it },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResultsRow(
    apps: List<AppInfo>,
    onAppClick: (AppInfo) -> Unit,
    onAppFocusChange: (AppInfo?) -> Unit = {},
) {
    Column(modifier = Modifier.padding(top = 24.dp)) {
        Text(
            text = if (apps.isEmpty()) "No installed apps match — press Search to look on YouTube" else "Apps",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(start = 48.dp, bottom = 12.dp),
        )

        if (apps.isNotEmpty()) {
            LazyRow(
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 48.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                items(apps, key = { it.packageName }) { app ->
                    AppTile(
                        app = app,
                        onClick = { onAppClick(app) },
                        modifier = Modifier.width(140.dp).height(160.dp),
                        onFocusChange = onAppFocusChange,
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeHeader(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearchSubmit: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 48.dp, vertical = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = stringResource(id = R.string.company_name),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            SearchBar(
                query = query,
                onQueryChange = onQueryChange,
                onSearchSubmit = onSearchSubmit,
                modifier = Modifier.width(400.dp),
            )

            DateTimeText(modifier = Modifier.padding(start = 20.dp))
        }
    }
}

/** Updates every 30s — plenty for a clock display, no need for per-second ticks. */
@Composable
private fun DateTimeText(modifier: Modifier = Modifier) {
    var now by remember { mutableStateOf(LocalDateTime.now()) }
    LaunchedEffect(Unit) {
        while (true) {
            now = LocalDateTime.now()
            delay(30_000)
        }
    }
    val formatter = remember { DateTimeFormatter.ofPattern("EEE, MMM d · h:mm a") }

    Text(
        text = now.format(formatter),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier,
    )
}
