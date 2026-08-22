package com.elegantgalaxy.tvlauncher.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.net.Uri
import android.provider.Settings

/**
 * Backing actions for the Settings screen rows. Each one either drives a
 * real system API directly (volume, brightness — the launcher already holds
 * WRITE_SETTINGS for this) or deep-links into the platform's own screen for
 * things a TV launcher shouldn't reimplement (Wi-Fi picker).
 */
object SettingsActions {

    private val brightnessLevels = intArrayOf(64, 128, 192, 255)

    /** Pops the system volume overlay, which the D-Pad/remote's volume keys already control. */
    fun adjustVolume(context: Context) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_SAME, AudioManager.FLAG_SHOW_UI)
    }

    /**
     * Steps screen brightness to the next of a few fixed levels, wrapping
     * back to the dimmest. Requests the WRITE_SETTINGS app-op if the user
     * hasn't granted it yet, since that grant can't be requested at install
     * time on API 23+.
     */
    fun cycleBrightness(context: Context) {
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
        val current = Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS, brightnessLevels.first())
        val next = brightnessLevels.firstOrNull { it > current } ?: brightnessLevels.first()
        Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS, next)
    }

    /** Hands off to the platform's own Wi-Fi settings screen rather than reimplementing a network picker. */
    fun openNetworkSettings(context: Context) {
        val intent = Intent(Settings.ACTION_WIFI_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (context.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
            context.startActivity(intent)
        }
    }
}
