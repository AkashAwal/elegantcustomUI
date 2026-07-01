package com.elegantgalaxy.tvlauncher.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.elegantgalaxy.tvlauncher.R
import com.elegantgalaxy.tvlauncher.model.AppCategory
import com.elegantgalaxy.tvlauncher.model.AppInfo
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        HomeHeader(onOpenSettings = onOpenSettings)

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

@Composable
private fun HomeHeader(onOpenSettings: () -> Unit) {
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

        IconButton(onClick = onOpenSettings) {
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = "Settings",
                tint = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}
