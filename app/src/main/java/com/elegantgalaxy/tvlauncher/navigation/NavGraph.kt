package com.elegantgalaxy.tvlauncher.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.elegantgalaxy.tvlauncher.ui.components.RailDestination
import com.elegantgalaxy.tvlauncher.ui.components.SideNavRail
import com.elegantgalaxy.tvlauncher.ui.home.HomeScreen
import com.elegantgalaxy.tvlauncher.ui.settings.SettingsSidebar

/**
 * The nav rail lives here, outside the screen content, so it stays on-screen
 * while Home occupies the content area beside it.
 *
 * Settings (DISPLAY_SETTINGS/NETWORK_SETTINGS/SETTINGS rail icons) opens an
 * overlay sidebar next to the rail instead of navigating to a full page —
 * Home stays mounted underneath. CONTACT has no destination at all — see
 * below — clicking it still highlights it, but is otherwise a no-op.
 */
@Composable
fun TvLauncherNavGraph(
    navController: NavHostController = rememberNavController()
) {
    var selectedRail by remember { mutableStateOf(RailDestination.SEARCH) }
    var settingsSidebarOpen by remember { mutableStateOf(false) }

    BackHandler(enabled = settingsSidebarOpen) { settingsSidebarOpen = false }

    Box(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxSize()) {
            SideNavRail(
                selected = selectedRail,
                onSelect = { destination ->
                    selectedRail = destination
                    when (destination) {
                        RailDestination.DISPLAY_SETTINGS,
                        RailDestination.NETWORK_SETTINGS,
                        RailDestination.SETTINGS -> settingsSidebarOpen = true
                        RailDestination.CONTACT -> Unit
                        else -> {
                            settingsSidebarOpen = false
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route)
                                launchSingleTop = true
                            }
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
            }
        }

        if (settingsSidebarOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(
                        indication = null,
                        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                        onClick = { settingsSidebarOpen = false },
                    ),
            )
        }

        AnimatedVisibility(
            visible = settingsSidebarOpen,
            enter = slideInHorizontally(initialOffsetX = { -it }),
            exit = slideOutHorizontally(targetOffsetX = { -it }),
            modifier = Modifier.align(Alignment.CenterStart),
        ) {
            SettingsSidebar()
        }
    }
}
