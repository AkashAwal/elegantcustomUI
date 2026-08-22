package com.elegantgalaxy.tvlauncher.ui.home

import android.app.Activity
import android.content.pm.PackageManager
import android.speech.RecognizerIntent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
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
 *
 * The query is edited via cursor index rather than always appending, so the
 * keyboard's left/right chevrons can move the insertion point like a real
 * text cursor.
 */
@Composable
fun SearchOverlay(homeViewModel: HomeViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val uiState by homeViewModel.uiState.collectAsState()
    val firstKeyFocusRequester = remember { FocusRequester() }
    val searchFieldFocusRequester = remember { FocusRequester() }

    var cursorIndex by remember { mutableIntStateOf(uiState.searchQuery.length) }
    var capsLock by remember { mutableStateOf(false) }
    var symbolsMode by remember { mutableStateOf(false) }
    var keyboardVisible by remember { mutableStateOf(true) }
    var showAllApps by remember { mutableStateOf(false) }

    // "More Apps" opens a full list within this overlay; Back should close
    // that first rather than closing the whole overlay in one press.
    BackHandler(enabled = showAllApps) { showAllApps = false }

    LaunchedEffect(Unit) {
        SearchHistoryStore.ensureInitialized(context)
        firstKeyFocusRequester.requestFocus()
    }
    val history by SearchHistoryStore.history.collectAsState()

    fun setQuery(newQuery: String, newCursor: Int) {
        homeViewModel.onSearchQueryChange(newQuery)
        cursorIndex = newCursor.coerceIn(0, newQuery.length)
    }

    fun insertChar(char: Char) {
        val query = uiState.searchQuery
        val next = query.substring(0, cursorIndex) + char + query.substring(cursorIndex)
        setQuery(next, cursorIndex + 1)
    }

    fun backspace() {
        if (cursorIndex == 0) return
        val query = uiState.searchQuery
        val next = query.substring(0, cursorIndex - 1) + query.substring(cursorIndex)
        setQuery(next, cursorIndex - 1)
    }

    fun submit() {
        if (uiState.searchQuery.isNotBlank()) {
            SearchHistoryStore.record(context, uiState.searchQuery)
            AppLauncherUtils.launchYouTubeSearch(context, uiState.searchQuery)
        }
    }

    val voiceLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
                ?.let { setQuery(it, it.length) }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.9f)),
    ) {
        SearchField(
            query = uiState.searchQuery,
            cursorIndex = cursorIndex,
            onClick = { keyboardVisible = true },
            focusRequester = searchFieldFocusRequester,
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            when {
                showAllApps -> {
                    AppGrid(
                        apps = uiState.allApps,
                        onAppClick = { app -> AppLauncherUtils.launch(context, app) },
                        modifier = Modifier.fillMaxSize(),
                    )
                }
                uiState.searchQuery.isBlank() -> {
                    BrowseSuggestions(
                        history = history,
                        onHistoryClick = { setQuery(it, it.length) },
                        onTrendingClick = { setQuery(it, it.length) },
                        onMoreAppsClick = { showAllApps = true },
                    )
                }
                else -> {
                    AppGrid(
                        apps = uiState.searchResults,
                        onAppClick = { app -> AppLauncherUtils.launch(context, app) },
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }

        AnimatedVisibility(visible = keyboardVisible) {
            TvKeyboard(
                capsLock = capsLock,
                onToggleCaps = { capsLock = !capsLock },
                symbolsMode = symbolsMode,
                onToggleSymbols = { symbolsMode = !symbolsMode },
                onCharPress = { char -> insertChar(char) },
                onBackspace = { backspace() },
                onClearAll = { setQuery("", 0) },
                onMoveCursor = { delta -> cursorIndex = (cursorIndex + delta).coerceIn(0, uiState.searchQuery.length) },
                onDone = { submit() },
                onMicPress = {
                    val intent = android.content.Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                        putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to search")
                    }
                    if (context.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                        voiceLauncher.launch(intent)
                    }
                },
                onHideKeyboard = {
                    keyboardVisible = false
                    searchFieldFocusRequester.requestFocus()
                },
                firstKeyFocusRequester = firstKeyFocusRequester,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.4f),
            )
        }
    }
}

@Composable
private fun SearchField(
    query: String,
    cursorIndex: Int,
    onClick: () -> Unit,
    focusRequester: FocusRequester,
) {
    val shape = RoundedCornerShape(28.dp)
    val focusVisuals = rememberTvFocusVisuals(shape = shape)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 48.dp, vertical = 24.dp)
            .focusRequester(focusRequester)
            .then(focusVisuals.modifier)
            .clip(shape)
            .background(SurfaceElevated2)
            .clickable(interactionSource = focusVisuals.interactionSource, indication = null, onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (query.isEmpty()) {
            Text(
                text = "Search apps, movies, YouTube…",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(start = 12.dp)
                    .weight(1f),
            )
        } else {
            Row(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .weight(1f),
            ) {
                Text(
                    text = query.substring(0, cursorIndex),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge,
                )
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(22.dp)
                        .background(MaterialTheme.colorScheme.primary),
                )
                Text(
                    text = query.substring(cursorIndex),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }
}

@Composable
private fun BrowseSuggestions(
    history: List<String>,
    onHistoryClick: (String) -> Unit,
    onTrendingClick: (String) -> Unit,
    onMoreAppsClick: () -> Unit,
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 48.dp, vertical = 6.dp),
    ) {
        if (history.isNotEmpty()) {
            SectionLabel("Previously Searched")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(history) { term ->
                    Chip(label = term, onClick = { onHistoryClick(term) })
                }
            }
            Spacer(Modifier.height(14.dp))
        }

        SectionLabel("Trending Searches")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(TRENDING_SEARCHES) { term ->
                Chip(label = term, onClick = { onTrendingClick(term) })
            }
        }

        Spacer(Modifier.height(14.dp))

        SectionLabel("Trending Apps")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(FamousAppsList) { app ->
                FamousAppTile(app = app, onClick = { AppLauncherUtils.launchOrOpenStore(context, app.packageName) })
            }
        }

        Spacer(Modifier.height(8.dp))

        MoreAppsLink(onClick = onMoreAppsClick)
    }
}

@Composable
private fun MoreAppsLink(onClick: () -> Unit) {
    val focusVisuals = rememberTvFocusVisuals(shape = RoundedCornerShape(6.dp))

    Row(
        modifier = Modifier
            .then(focusVisuals.modifier)
            .clip(RoundedCornerShape(6.dp))
            .clickable(interactionSource = focusVisuals.interactionSource, indication = null, onClick = onClick)
            .padding(vertical = 6.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "More Apps",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .padding(start = 6.dp)
                .size(16.dp),
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(bottom = 8.dp),
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
            .padding(horizontal = 16.dp, vertical = 7.dp),
    ) {
        Text(text = label, color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun FamousAppTile(app: FamousApp, onClick: () -> Unit) {
    val context = LocalContext.current
    val shape = RoundedCornerShape(12.dp)
    val focusVisuals = rememberTvFocusVisuals(shape = shape)
    val realIcon = remember(app.packageName) { AppLauncherUtils.getInstalledAppIcon(context, app.packageName) }

    Box(
        modifier = Modifier
            .then(focusVisuals.modifier)
            .size(width = 140.dp, height = 80.dp)
            .clip(shape)
            .background(if (realIcon != null) SurfaceElevated3 else app.color)
            .clickable(interactionSource = focusVisuals.interactionSource, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (realIcon != null) {
            Image(
                bitmap = realIcon.asImageBitmap(),
                contentDescription = app.label,
                modifier = Modifier.size(56.dp),
            )
        } else {
            Text(
                text = app.label,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleSmall,
            )
        }
    }
}
