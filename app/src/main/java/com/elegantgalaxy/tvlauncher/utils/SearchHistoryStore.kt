package com.elegantgalaxy.tvlauncher.utils

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Persists recently-submitted search queries, most recent first, capped at
 * [MAX_ENTRIES]. Plain SharedPreferences, same lightweight pattern as
 * [DisplayModeStore]/[QuickSettingsStore] — no new dependency. Entries are
 * joined with a control character (not a space) since search terms can
 * themselves contain spaces.
 */
object SearchHistoryStore {
    private const val PREFS_NAME = "search_history_prefs"
    private const val KEY_HISTORY = "history"
    private const val MAX_ENTRIES = 8
    private val DELIMITER = Char(1).toString()

    private var initialized = false
    private val _history = MutableStateFlow<List<String>>(emptyList())
    val history: StateFlow<List<String>> = _history.asStateFlow()

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun ensureInitialized(context: Context) {
        if (initialized) return
        val raw = prefs(context).getString(KEY_HISTORY, null)
        _history.value = raw?.split(DELIMITER)?.filter { it.isNotBlank() } ?: emptyList()
        initialized = true
    }

    fun record(context: Context, query: String) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return
        val updated = (listOf(trimmed) + _history.value.filterNot { it.equals(trimmed, ignoreCase = true) })
            .take(MAX_ENTRIES)
        _history.value = updated
        prefs(context).edit().putString(KEY_HISTORY, updated.joinToString(DELIMITER)).apply()
    }
}
