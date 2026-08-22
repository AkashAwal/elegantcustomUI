package com.elegantgalaxy.tvlauncher.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.elegantgalaxy.tvlauncher.ui.components.SearchBar
import com.elegantgalaxy.tvlauncher.ui.theme.SurfaceElevated1
import com.elegantgalaxy.tvlauncher.utils.AppLauncherUtils

/**
 * Search entry point, opened as an overlay sidebar next to the nav rail —
 * same pattern as [com.elegantgalaxy.tvlauncher.ui.settings.SettingsSidebar]
 * — rather than a search bar fixed atop Home. Shares [homeViewModel] with
 * [HomeScreen] so typing here drives the same search-results grid Home
 * already renders in its content area beside this sidebar.
 */
@Composable
fun SearchSidebar(homeViewModel: HomeViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val uiState by homeViewModel.uiState.collectAsState()
    val focusRequester = remember { FocusRequester() }

    // Grabs D-Pad focus as soon as the sidebar opens, since there's no
    // touchscreen to tap the field into focus on a TV remote.
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(300.dp)
            .background(SurfaceElevated1)
            .padding(16.dp),
    ) {
        Text(
            text = "Search",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
        )

        SearchBar(
            query = uiState.searchQuery,
            onQueryChange = homeViewModel::onSearchQueryChange,
            onSearchSubmit = { AppLauncherUtils.launchYouTubeSearch(context, uiState.searchQuery) },
            focusRequester = focusRequester,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp),
        )
    }
}
