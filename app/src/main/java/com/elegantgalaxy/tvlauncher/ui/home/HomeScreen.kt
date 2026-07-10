package com.elegantgalaxy.tvlauncher.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.elegantgalaxy.tvlauncher.R
import com.elegantgalaxy.tvlauncher.model.AppCategory
import com.elegantgalaxy.tvlauncher.model.AppInfo
import com.elegantgalaxy.tvlauncher.ui.components.AppTile
import com.elegantgalaxy.tvlauncher.ui.components.GoldenParticleBackground
import com.elegantgalaxy.tvlauncher.ui.components.SearchBar
import com.elegantgalaxy.tvlauncher.utils.AppLauncherUtils
import com.elegantgalaxy.tvlauncher.utils.MockData

/**
 * Launcher home screen: branding header, then one horizontally-scrolling
 * [AppCarousel] per [AppCategory] that has apps. Swap [MockData.sampleApps]
 * for `AppLauncherUtils.queryLaunchableApps(context)` once you're testing
 * against a real device's installed app list.
 */
@Composable
fun HomeScreen(
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val apps = remember { MockData.sampleApps }
    val appsByCategory = remember(apps) { apps.groupBy { it.category } }

    var query by remember { mutableStateOf("") }
    val filteredApps = remember(apps, query) {
        if (query.isBlank()) emptyList() else apps.filter { it.label.contains(query, ignoreCase = true) }
    }

    Box(modifier = modifier.fillMaxSize()) {
        GoldenParticleBackground(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.6f)),
        ) {
            HomeHeader(
                onOpenSettings = onOpenSettings,
                query = query,
                onQueryChange = { query = it },
                onSearchSubmit = { AppLauncherUtils.launchYouTubeSearch(context, query) },
            )

            if (query.isNotBlank()) {
                SearchResultsRow(
                    apps = filteredApps,
                    onAppClick = { app -> AppLauncherUtils.launch(context, app) },
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(32.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 16.dp),
                ) {
                    items(AppCategory.entries.filter { appsByCategory[it]?.isNotEmpty() == true }) { category ->
                        AppCarousel(
                            title = category.displayName,
                            apps = appsByCategory[category].orEmpty(),
                            onAppClick = { app: AppInfo -> AppLauncherUtils.launch(context, app) },
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
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeHeader(
    onOpenSettings: () -> Unit,
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
        Row(verticalAlignment = Alignment.CenterVertically) {
            val companyName = stringResource(id = R.string.company_name)
            Image(
                painter = painterResource(id = R.drawable.company_logo),
                contentDescription = companyName,
                modifier = Modifier.size(40.dp),
            )
            Spacer(modifier = Modifier.padding(start = 12.dp))
            Text(
                text = companyName,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }

        SearchBar(
            query = query,
            onQueryChange = onQueryChange,
            onSearchSubmit = onSearchSubmit,
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .width(420.dp),
        )

        IconButton(onClick = onOpenSettings) {
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = "Settings",
                tint = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}
