package com.elegantgalaxy.tvlauncher.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.elegantgalaxy.tvlauncher.model.AppCategory
import com.elegantgalaxy.tvlauncher.model.AppInfo
import com.elegantgalaxy.tvlauncher.utils.AppLauncherUtils

/**
 * The [FamousAppsList] apps that aren't on this device, as tiles for the home "Apps" lane. Each uses
 * its bundled `ic_brand_<key>` logo when present (letter fallback otherwise) and is identified by its
 * package name, so the caller can tell a suggestion from an installed app and open the install panel.
 */
@Composable
fun rememberSuggestedApps(installed: List<AppInfo>): List<AppInfo> {
    val context = LocalContext.current
    val missing = remember(installed) {
        val have = installed.mapTo(HashSet()) { it.packageName }
        FamousAppsList.filter { it.packageName !in have && !AppLauncherUtils.isInstalled(context, it.packageName) }
    }
    return missing.map { app ->
        val logo = context.resources.getIdentifier("ic_brand_${app.key}", "drawable", context.packageName)
        AppInfo(
            packageName = app.packageName,
            activityName = "",
            label = app.label,
            category = AppCategory.STREAMING,
            icon = if (logo != 0) painterResource(logo) else null,
        )
    }
}
