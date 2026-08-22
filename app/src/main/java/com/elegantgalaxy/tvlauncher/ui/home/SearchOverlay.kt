package com.elegantgalaxy.tvlauncher.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.elegantgalaxy.tvlauncher.ui.theme.SurfaceElevated2
import com.elegantgalaxy.tvlauncher.ui.theme.SurfaceElevated3
import com.elegantgalaxy.tvlauncher.utils.AppLauncherUtils
import com.elegantgalaxy.tvlauncher.utils.SearchHistoryStore
import com.elegantgalaxy.tvlauncher.utils.rememberTvFocusVisuals

/**
 * Curated, static list — there's no live "trending" data source for this
 * launcher to pull from, so these are fixed placeholder terms rather than
 * anything reflecting real usage.
 */
private val TRENDING_SEARCHES = listOf("Cricket", "New movies", "Comedy specials", "News", "Kids shows")

/**
 * Full-screen search — a translucent scrim over all of Home rather than a
 * sidebar, with the field on top and [TvKeyboard] pinned to the bottom
 * (~40% of height). Empty query shows Previously Searched / Trending /
 * Famous Apps; typing switches to a live results grid of real installed
 * apps. Shares [homeViewModel] so the query/results state is the same one
 * [HomeScreen] would show if this overlay were closed.
 */
@Composable
fun SearchOverlay(homeViewModel: HomeViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val uiState by homeViewModel.uiState.collectAsState()
    val firstKeyFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        SearchHistoryStore.ensureInitialized(context)
        firstKeyFocusRequester.requestFocus()
    }
    val history by SearchHistoryStore.history.collectAsState()

    fun submit(query: String) {
        homeViewModel.onSearchQueryChange(query)
        SearchHistoryStore.record(context, query)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.9f)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 48.dp, vertical = 24.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(SurfaceElevated2)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = uiState.searchQuery.ifEmpty { "Search apps, movies, YouTube…" },
                color = if (uiState.searchQuery.isEmpty()) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(start = 12.dp)
                    .weight(1f),
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            if (uiState.searchQuery.isBlank()) {
                BrowseSuggestions(
                    history = history,
                    onHistoryClick = { submit(it) },
                    onTrendingClick = { submit(it) },
                )
            } else {
                AppGrid(
                    apps = uiState.searchResults,
                    onAppClick = { app -> AppLauncherUtils.launch(context, app) },
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

        TvKeyboard(
            onKeyPress = { char -> homeViewModel.onSearchQueryChange(uiState.searchQuery + char) },
            onBackspace = { homeViewModel.onSearchQueryChange(uiState.searchQuery.dropLast(1)) },
            onSearch = {
                if (uiState.searchQuery.isNotBlank()) {
                    SearchHistoryStore.record(context, uiState.searchQuery)
                    AppLauncherUtils.launchYouTubeSearch(context, uiState.searchQuery)
                }
            },
            firstKeyFocusRequester = firstKeyFocusRequester,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.4f),
        )
    }
}

@Composable
private fun BrowseSuggestions(
    history: List<String>,
    onHistoryClick: (String) -> Unit,
    onTrendingClick: (String) -> Unit,
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 48.dp, vertical = 8.dp),
    ) {
        if (history.isNotEmpty()) {
            SectionLabel("Previously Searched")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(history) { term ->
                    Chip(label = term, onClick = { onHistoryClick(term) })
                }
            }
            Spacer(Modifier.height(28.dp))
        }

        SectionLabel("Trending Searches")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(TRENDING_SEARCHES) { term ->
                Chip(label = term, onClick = { onTrendingClick(term) })
            }
        }

        Spacer(Modifier.height(28.dp))

        SectionLabel("Famous Apps")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(FamousAppsList) { app ->
                FamousAppTile(app = app, onClick = { AppLauncherUtils.launchOrOpenStore(context, app.packageName) })
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(bottom = 12.dp),
    )
}

@Composable
private fun Chip(label: String, onClick: () -> Unit) {
    val shape = RoundedCornerShape(20.dp)
    val focusVisuals = rememberTvFocusVisuals(shape = shape)

    Box(
        modifier = Modifier
            .then(focusVisuals.modifier)
            .clip(shape)
            .background(SurfaceElevated3)
            .clickable(interactionSource = focusVisuals.interactionSource, indication = null, onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 10.dp),
    ) {
        Text(text = label, color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun FamousAppTile(app: FamousApp, onClick: () -> Unit) {
    val shape = RoundedCornerShape(12.dp)
    val focusVisuals = rememberTvFocusVisuals(shape = shape)

    Box(
        modifier = Modifier
            .then(focusVisuals.modifier)
            .size(width = 140.dp, height = 80.dp)
            .clip(shape)
            .background(app.color)
            .clickable(interactionSource = focusVisuals.interactionSource, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = app.label,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleSmall,
        )
    }
}
