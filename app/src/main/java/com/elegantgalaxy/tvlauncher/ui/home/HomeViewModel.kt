package com.elegantgalaxy.tvlauncher.ui.home

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.elegantgalaxy.tvlauncher.model.AppCategory
import com.elegantgalaxy.tvlauncher.model.AppInfo
import com.elegantgalaxy.tvlauncher.utils.AppLauncherUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class HomeUiState(
    val isLoading: Boolean = true,
    val allApps: List<AppInfo> = emptyList(),
    val searchQuery: String = "",
    val focusedApp: AppInfo? = null,
) {
    val appsByCategory: Map<AppCategory, List<AppInfo>>
        get() = allApps.groupBy { it.category }

    val searchResults: List<AppInfo>
        get() = allApps.filter { it.label.contains(searchQuery, ignoreCase = true) }
}

/**
 * Owns the installed-app list so PackageManager queries and icon decoding
 * run off the main thread and survive configuration changes. Also listens
 * for package add/remove/replace broadcasts so the launcher picks up newly
 * (un)installed apps without needing a restart.
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val packageChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) = loadApps()
    }

    init {
        loadApps()
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addDataScheme("package")
        }
        application.registerReceiver(packageChangeReceiver, filter)
    }

    private fun loadApps() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val apps = withContext(Dispatchers.IO) {
                AppLauncherUtils.queryLaunchableApps(getApplication())
            }
            _uiState.update { it.copy(isLoading = false, allApps = apps) }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onFocusedAppChange(app: AppInfo?) {
        _uiState.update { it.copy(focusedApp = app) }
    }

    override fun onCleared() {
        getApplication<Application>().unregisterReceiver(packageChangeReceiver)
        super.onCleared()
    }
}
