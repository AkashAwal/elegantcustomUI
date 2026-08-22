package com.elegantgalaxy.tvlauncher.utils

import android.content.Context
import com.elegantgalaxy.tvlauncher.ui.settings.SleepTimer
import com.elegantgalaxy.tvlauncher.ui.settings.SoundMode
import com.elegantgalaxy.tvlauncher.ui.settings.SoundOutput
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Persists the Settings sidebar's Sound Mode / Sound Output / Sleep Timer /
 * Eye Care switchers. Plain SharedPreferences, same pattern as
 * [DisplayModeStore] — no new dependency for a handful of small keys.
 *
 * Unlike Brightness (in [SettingsActions], which drives the real system
 * value), these don't affect real hardware yet: there's no public Android
 * API for a launcher app to control TV sound EQ/output routing, and
 * triggering device sleep requires a system-signature permission this app
 * doesn't hold. The switchers are fully functional and persist real user
 * choices — the choices just aren't wired to hardware effects yet.
 */
object QuickSettingsStore {
    private const val PREFS_NAME = "quick_settings_prefs"
    private const val KEY_SOUND_MODE = "sound_mode"
    private const val KEY_SOUND_OUTPUT = "sound_output"
    private const val KEY_SLEEP_TIMER = "sleep_timer"
    private const val KEY_EYE_CARE = "eye_care"

    private var initialized = false
    private val _soundMode = MutableStateFlow(SoundMode.NORMAL)
    private val _soundOutput = MutableStateFlow(SoundOutput.TV_SPEAKER)
    private val _sleepTimer = MutableStateFlow(SleepTimer.OFF)
    private val _eyeCareEnabled = MutableStateFlow(false)

    val soundMode: StateFlow<SoundMode> = _soundMode.asStateFlow()
    val soundOutput: StateFlow<SoundOutput> = _soundOutput.asStateFlow()
    val sleepTimer: StateFlow<SleepTimer> = _sleepTimer.asStateFlow()
    val eyeCareEnabled: StateFlow<Boolean> = _eyeCareEnabled.asStateFlow()

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun ensureInitialized(context: Context) {
        if (initialized) return
        val prefs = prefs(context)
        _soundMode.value = SoundMode.entries.firstOrNull { it.name == prefs.getString(KEY_SOUND_MODE, null) }
            ?: SoundMode.NORMAL
        _soundOutput.value = SoundOutput.entries.firstOrNull { it.name == prefs.getString(KEY_SOUND_OUTPUT, null) }
            ?: SoundOutput.TV_SPEAKER
        _sleepTimer.value = SleepTimer.entries.firstOrNull { it.name == prefs.getString(KEY_SLEEP_TIMER, null) }
            ?: SleepTimer.OFF
        _eyeCareEnabled.value = prefs.getBoolean(KEY_EYE_CARE, false)
        initialized = true
    }

    fun setSoundMode(context: Context, mode: SoundMode) {
        _soundMode.value = mode
        prefs(context).edit().putString(KEY_SOUND_MODE, mode.name).apply()
    }

    fun setSoundOutput(context: Context, output: SoundOutput) {
        _soundOutput.value = output
        prefs(context).edit().putString(KEY_SOUND_OUTPUT, output.name).apply()
    }

    fun setSleepTimer(context: Context, timer: SleepTimer) {
        _sleepTimer.value = timer
        prefs(context).edit().putString(KEY_SLEEP_TIMER, timer.name).apply()
    }

    fun setEyeCareEnabled(context: Context, enabled: Boolean) {
        _eyeCareEnabled.value = enabled
        prefs(context).edit().putBoolean(KEY_EYE_CARE, enabled).apply()
    }
}
