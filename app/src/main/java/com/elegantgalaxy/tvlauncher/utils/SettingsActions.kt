package com.elegantgalaxy.tvlauncher.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.net.wifi.WifiManager
import android.provider.Settings

/** Backing actions for the Settings sidebar's Brightness switcher — the one item that drives a real system value. */
object SettingsActions {

    /** Fixed dot-selectable levels, matching the discrete-option pattern every other sidebar tile uses. */
    val brightnessLevels = listOf(20, 40, 60, 80, 100)

    /** Current screen brightness as 0-100, reading the real system value. */
    fun getBrightnessPercent(context: Context): Int {
        val raw = Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS, 128)
        return (raw * 100 / 255).coerceIn(0, 100)
    }

    /**
     * Sets screen brightness to [percent]. Requests the WRITE_SETTINGS
     * app-op if the user hasn't granted it yet, since that grant can't be
     * requested at install time on API 23+ — guarded with resolveActivity
     * since not every system image ships that settings screen.
     */
    fun setBrightnessPercent(context: Context, percent: Int) {
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
        Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS, percent.coerceIn(5, 100) * 255 / 100)
    }

    /** Hands off to the platform's own Wi-Fi settings screen rather than reimplementing a network picker. */
    fun openNetworkSettings(context: Context) {
        val intent = Intent(Settings.ACTION_WIFI_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (context.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
            context.startActivity(intent)
        }
    }

    /**
     * The connected Wi-Fi network's name, or "Disconnected" if there's no
     * active Wi-Fi connection. On API 29+, the real SSID is only readable
     * with a location permission granted — this app doesn't request one
     * (a TV launcher has no legitimate reason to ask for location), so on
     * those versions this falls back to "Connected" when the OS masks the
     * SSID as unknown, rather than showing that placeholder string.
     */
    fun getWifiStatusLabel(context: Context): String {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return "Disconnected"
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return "Disconnected"
        if (!capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) return "Disconnected"

        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val ssid = wifiManager.connectionInfo?.ssid?.trim('"')
        return if (ssid.isNullOrBlank() || ssid == WifiManager.UNKNOWN_SSID) "Connected" else ssid
    }
}
