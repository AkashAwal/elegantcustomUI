package com.elegantgalaxy.tvlauncher.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings

/** Backing actions for the Settings sidebar's Brightness switcher — the one item that drives a real system value. */
object SettingsActions {

    private const val BRIGHTNESS_STEP_PERCENT = 10

    /** Current screen brightness as 0-100, reading the real system value. */
    fun getBrightnessPercent(context: Context): Int {
        val raw = Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS, 128)
        return (raw * 100 / 255).coerceIn(0, 100)
    }

    fun increaseBrightness(context: Context) = stepBrightness(context, BRIGHTNESS_STEP_PERCENT)

    fun decreaseBrightness(context: Context) = stepBrightness(context, -BRIGHTNESS_STEP_PERCENT)

    /**
     * Steps screen brightness by [deltaPercent]. Requests the WRITE_SETTINGS
     * app-op if the user hasn't granted it yet, since that grant can't be
     * requested at install time on API 23+ — guarded with resolveActivity
     * since not every system image ships that settings screen.
     */
    private fun stepBrightness(context: Context, deltaPercent: Int) {
        if (!Settings.System.canWrite(context)) {
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:${context.packageName}"))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (context.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                context.startActivity(intent)
            }
            return
        }
        val resolver = context.contentResolver
        Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL)
        val nextPercent = (getBrightnessPercent(context) + deltaPercent).coerceIn(5, 100)
        Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS, nextPercent * 255 / 100)
    }
}
