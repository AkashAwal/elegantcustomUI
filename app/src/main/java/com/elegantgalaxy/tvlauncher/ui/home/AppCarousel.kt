package com.elegantgalaxy.tvlauncher.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.elegantgalaxy.tvlauncher.model.AppCategory
import com.elegantgalaxy.tvlauncher.model.AppInfo
import com.elegantgalaxy.tvlauncher.ui.components.AppTile

/**
 * One horizontally-scrolling row of apps for a single [AppCategory].
 * The home screen stacks several of these; LazyRow only composes tiles
 * near the viewport, which matters on 1-2GB RAM devices with large
 * app catalogues.
 */
@Composable
fun AppCarousel(
    title: String,
    apps: List<AppInfo>,
    onAppClick: (AppInfo) -> Unit,
    modifier: Modifier = Modifier,
    onAppFocusChange: (AppInfo?) -> Unit = {},
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(start = 48.dp, bottom = 12.dp),
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 48.dp),
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
