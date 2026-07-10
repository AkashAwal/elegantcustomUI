package com.elegantgalaxy.tvlauncher.navigation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.elegantgalaxy.tvlauncher.ui.components.RailDestination
import com.elegantgalaxy.tvlauncher.ui.components.SideNavRail
import com.elegantgalaxy.tvlauncher.ui.home.HomeScreen
import com.elegantgalaxy.tvlauncher.ui.settings.SettingsScreen

/**
 * The nav rail lives here, outside both screens, so it stays on-screen while
 * Home/Settings swap in the content area beside it rather than being part
 * of either screen's own layout.
 */
@Composable
fun TvLauncherNavGraph(
    navController: NavHostController = rememberNavController()
) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val selectedRail = if (currentRoute == Screen.Settings.route) RailDestination.SETTINGS else RailDestination.HOME

    Row(modifier = Modifier.fillMaxSize()) {
        SideNavRail(
            selected = selectedRail,
            onSelect = { destination ->
                // SEARCH/APPS/GUIDE have no dedicated screens yet — Home
                // already hosts the search bar and app grid, so route there
                // until those get their own destinations.
                val target = when (destination) {
                    RailDestination.SETTINGS -> Screen.Settings.route
                    else -> Screen.Home.route
                }
                if (target != currentRoute) {
                    navController.navigate(target) {
                        popUpTo(Screen.Home.route)
                        launchSingleTop = true
                    }
                }
            },
            // No Contact/Notifications screen exists yet — these are
            // visual placeholders until that's built.
            onContactClick = {},
            onNotificationsClick = {},
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
