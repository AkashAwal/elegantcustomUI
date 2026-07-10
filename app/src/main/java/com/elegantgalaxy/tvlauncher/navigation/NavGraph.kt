package com.elegantgalaxy.tvlauncher.navigation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.elegantgalaxy.tvlauncher.ui.components.RailDestination
import com.elegantgalaxy.tvlauncher.ui.components.SideNavRail
import com.elegantgalaxy.tvlauncher.ui.home.HomeScreen
import com.elegantgalaxy.tvlauncher.ui.settings.SettingsScreen

/**
 * The nav rail lives here, outside both screens, so it stays on-screen while
 * Home/Settings swap in the content area beside it rather than being part
 * of either screen's own layout.
 *
 * Selection is tracked as "last rail icon clicked" rather than derived from
 * the current route: SEARCH/APPS/GUIDE all route to Home, and
 * DISPLAY_SETTINGS/NETWORK_SETTINGS/SETTINGS all route to Settings, so
 * route alone can't tell those apart. CONTACT has no destination at all
 * (see below) — clicking it still highlights it, but is otherwise a no-op.
 */
@Composable
fun TvLauncherNavGraph(
    navController: NavHostController = rememberNavController()
) {
    var selectedRail by remember { mutableStateOf(RailDestination.SEARCH) }

    Row(modifier = Modifier.fillMaxSize()) {
        SideNavRail(
            selected = selectedRail,
            onSelect = { destination ->
                selectedRail = destination

                // SEARCH/APPS/GUIDE have no dedicated screens yet — Home
                // already hosts the search bar and app grid. CONTACT has no
                // screen at all — it's a visual placeholder for now.
                val target = when (destination) {
                    RailDestination.DISPLAY_SETTINGS,
                    RailDestination.NETWORK_SETTINGS,
                    RailDestination.SETTINGS -> Screen.Settings.route
                    RailDestination.CONTACT -> null
                    else -> Screen.Home.route
                }
                if (target != null) {
                    navController.navigate(target) {
                        popUpTo(Screen.Home.route)
                        launchSingleTop = true
                    }
                }
            },
        )

        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.weight(1f),
        ) {
            composable(Screen.Home.route) {
                HomeScreen()
            }
            composable(Screen.Settings.route) {
                SettingsScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
