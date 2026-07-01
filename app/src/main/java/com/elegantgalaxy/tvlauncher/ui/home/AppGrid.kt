package com.elegantgalaxy.tvlauncher.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.elegantgalaxy.tvlauncher.model.AppInfo
import com.elegantgalaxy.tvlauncher.ui.components.AppTile

/**
 * Grid-based "all apps" view. Uses [GridCells.Adaptive] instead of a fixed
 * column count so the same code renders correctly from a 24" to a 65" TV:
 * more physical screen width at the same density simply fits more 160dp
 * columns, no per-screen-size branching needed.
 */
@Composable
fun AppGrid(
    apps: List<AppInfo>,
    onAppClick: (AppInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        contentPadding = PaddingValues(horizontal = 48.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = modifier,
    ) {
        items(apps, key = { it.packageName }) { app ->
            AppTile(
                app = app,
                onClick = { onAppClick(app) },
                modifier = Modifier.aspectRatio(0.9f),
            )
        }
    }
}
