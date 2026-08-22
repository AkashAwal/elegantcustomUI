package com.elegantgalaxy.tvlauncher.utils

import android.content.Context
import com.elegantgalaxy.tvlauncher.ui.theme.DisplayMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Persists the selected [DisplayMode] across launches. Plain SharedPreferences
 * rather than DataStore — this is a single small key with no reactive-query
 * needs, so it doesn't justify pulling in another dependency.
 */
object DisplayModeStore {
    private const val PREFS_NAME = "display_mode_prefs"
    private const val KEY_MODE = "display_mode"

    private var initialized = false
    private val _mode = MutableStateFlow(DisplayMode.NORMAL)
    val mode: StateFlow<DisplayMode> = _mode.asStateFlow()

    private fun ensureInitialized(context: Context) {
        if (initialized) return
        val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val saved = prefs.getString(KEY_MODE, DisplayMode.NORMAL.name)
        _mode.value = DisplayMode.entries.firstOrNull { it.name == saved } ?: DisplayMode.NORMAL
        initialized = true
    }

    fun current(context: Context): DisplayMode {
        ensureInitialized(context)
        return _mode.value
    }

    fun setMode(context: Context, mode: DisplayMode) {
        ensureInitialized(context)
        _mode.value = mode
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_MODE, mode.name)
            .apply()
    }
}
