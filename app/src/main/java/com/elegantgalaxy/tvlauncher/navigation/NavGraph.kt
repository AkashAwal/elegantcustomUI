package com.elegantgalaxy.tvlauncher.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.elegantgalaxy.tvlauncher.ui.components.RailDestination
import com.elegantgalaxy.tvlauncher.ui.components.SideNavRail
import com.elegantgalaxy.tvlauncher.ui.home.HomeScreen
import com.elegantgalaxy.tvlauncher.ui.home.HomeViewModel
import com.elegantgalaxy.tvlauncher.ui.home.SearchOverlay
import com.elegantgalaxy.tvlauncher.ui.settings.SettingsSidebar

/**
 * The nav rail lives here, outside the screen content, so it stays on-screen
 * while Home occupies the content area beside it.
 *
 * [homeViewModel] is hoisted to this level (rather than owned inside
 * [HomeScreen]) so [SearchOverlay] — which lives outside the NavHost, as a
 * sibling overlay — can share the same search-query state.
 *
 * Settings (DISPLAY_SETTINGS/NETWORK_SETTINGS/SETTINGS rail icons) opens an
 * overlay sidebar next to the rail; Search (the SEARCH rail icon) opens a
 * full-screen overlay instead — unlike Settings, Home doesn't need to stay
 * visible behind it, since search results render inside the overlay itself.
 * Opening one closes the other. CONTACT has no destination at all —
 * clicking it still highlights it, but is otherwise a no-op.
 */
@Composable
fun TvLauncherNavGraph(
    navController: NavHostController = rememberNavController()
) {
    var selectedRail by remember { mutableStateOf(RailDestination.SEARCH) }
    var settingsSidebarOpen by remember { mutableStateOf(false) }
    var searchOverlayOpen by remember { mutableStateOf(false) }
    val homeViewModel: HomeViewModel = viewModel()

    fun closeSearch() {
        searchOverlayOpen = false
        homeViewModel.onSearchQueryChange("")
    }

    BackHandler(enabled = settingsSidebarOpen || searchOverlayOpen) {
        settingsSidebarOpen = false
        if (searchOverlayOpen) closeSearch()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxSize()) {
            SideNavRail(
                selected = selectedRail,
                onSelect = { destination ->
                    selectedRail = destination
                    when (destination) {
                        RailDestination.SEARCH -> {
                            settingsSidebarOpen = false
                            searchOverlayOpen = true
                        }
                        RailDestination.DISPLAY_SETTINGS,
                        RailDestination.NETWORK_SETTINGS,
                        RailDestination.SETTINGS -> {
                            if (searchOverlayOpen) closeSearch()
                            settingsSidebarOpen = true
                        }
                        RailDestination.CONTACT -> Unit
                        else -> {
                            settingsSidebarOpen = false
                            if (searchOverlayOpen) closeSearch()
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
                    HomeScreen(viewModel = homeViewModel)
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
                        interactionSource = remember { MutableInteractionSource() },
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

        AnimatedVisibility(
            visible = searchOverlayOpen,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            SearchOverlay(homeViewModel = homeViewModel)
        }
    }
}
