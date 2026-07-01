package com.elegantgalaxy.tvlauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import com.elegantgalaxy.tvlauncher.navigation.TvLauncherNavGraph
import com.elegantgalaxy.tvlauncher.ui.theme.ElegantGalaxyTvTheme
import androidx.compose.ui.Modifier
import androidx.compose.material3.MaterialTheme

/**
 * Single-activity launcher: all screens (home, settings) live inside the
 * Compose Navigation graph rather than separate Activities. This keeps
 * process/memory overhead low, which matters on the 1-2GB RAM chipsets
 * this launcher targets — every extra Activity is another window + view
 * hierarchy the system has to keep alive.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ElegantGalaxyTvTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    TvLauncherNavGraph()
                }
            }
        }
    }
}
