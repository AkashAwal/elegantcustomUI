package com.elegantgalaxy.tvlauncher.utils

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import com.elegantgalaxy.tvlauncher.model.AppCategory
import com.elegantgalaxy.tvlauncher.model.AppInfo

/**
 * Reads the real list of installed, launchable apps from PackageManager.
 * This replaces MockData once you're ready to run on an actual device —
 * swap the call site in the home screen ViewModel/state holder.
 */
object AppLauncherUtils {

    fun queryLaunchableApps(context: Context): List<AppInfo> {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER)
        }

        return pm.queryIntentActivities(intent, PackageManager.MATCH_ALL)
            .filter { it.activityInfo.packageName != context.packageName }
            .map { resolveInfo ->
                val activityInfo = resolveInfo.activityInfo
                val isSystem = (activityInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                val iconDrawable = resolveInfo.loadIcon(pm)
                val iconBitmap = iconDrawable.toBitmap()
                AppInfo(
                    packageName = activityInfo.packageName,
                    activityName = activityInfo.name,
                    label = resolveInfo.loadLabel(pm).toString(),
                    category = if (isSystem) AppCategory.TOOLS else AppCategory.STREAMING,
                    icon = BitmapPainter(iconBitmap.asImageBitmap()),
                    iconBitmap = iconBitmap,
                    isSystemApp = isSystem,
                )
            }
    }

    fun launch(context: Context, app: AppInfo) {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
            setClassName(app.packageName, app.activityName)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    private const val YOUTUBE_TV_PACKAGE = "com.google.android.youtube.tv"

    /**
     * Hands a search query off to the YouTube TV app's own search-results
     * screen. Deliberately not rendering YouTube results inline here — that
     * would need a Data API key, quota management, and a thumbnail-loading
     * layer for what the YouTube app already does natively. Falls back to a
     * web search intent if YouTube isn't installed.
     */
    fun launchYouTubeSearch(context: Context, query: String) {
        val youtubeIntent = Intent(Intent.ACTION_SEARCH).apply {
            setPackage(YOUTUBE_TV_PACKAGE)
            putExtra(SearchManager.QUERY, query)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val resolved = context.packageManager.resolveActivity(youtubeIntent, PackageManager.MATCH_DEFAULT_ONLY)
        if (resolved != null) {
            context.startActivity(youtubeIntent)
            return
        }

        val webIntent = Intent(Intent.ACTION_VIEW).apply {
            data = android.net.Uri.parse("https://www.youtube.com/results?search_query=${android.net.Uri.encode(query)}")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(webIntent)
    }

    /**
     * Launches [packageName] if it's actually installed, otherwise sends the
     * user to its Play Store listing (app first, falling back to the web
     * listing if the Play Store app itself isn't present). Backs the
     * "Famous Apps" curated shortcuts in search, which list well-known apps
     * regardless of whether this specific device has them installed.
     */
    fun launchOrOpenStore(context: Context, packageName: String) {
        val launchIntent = context.packageManager.getLeanbackLaunchIntentForPackage(packageName)
            ?: context.packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(launchIntent)
            return
        }

        val marketIntent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse("market://details?id=$packageName"))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (context.packageManager.resolveActivity(marketIntent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
            context.startActivity(marketIntent)
            return
        }

        val webIntent = Intent(
            Intent.ACTION_VIEW,
            android.net.Uri.parse("https://play.google.com/store/apps/details?id=$packageName"),
        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(webIntent)
    }
}

private fun android.graphics.drawable.Drawable.toBitmap(): android.graphics.Bitmap {
    if (this is android.graphics.drawable.BitmapDrawable) return bitmap
    val width = intrinsicWidth.coerceAtLeast(1)
    val height = intrinsicHeight.coerceAtLeast(1)
    val bitmap = android.graphics.Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)
    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)
    return bitmap
}
