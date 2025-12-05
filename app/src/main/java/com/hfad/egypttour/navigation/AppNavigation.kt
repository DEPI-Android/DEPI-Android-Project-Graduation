package com.hfad.egypttour.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.hfad.egypttour.ui.screens.GovernorateListScreen
import com.hfad.egypttour.ui.screens.*
import com.hfad.egypttour.ui.screens.LandmarksListScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument

/**
 * Main navigation setup for Egypt Tour app
 */
object NavigationDestinations {
    const val GOVERNORATE_LIST = "governorates"
    const val LANDMARK_LIST = "landmarks/{governorateId}"
    const val LANDMARK_DETAIL = "landmark/{landmarkId}"
    const val PROFILE = "profile"
    const val SAVED_LIST = "saved_list/{type}"
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    isDarkMode: Boolean = false,
    onDarkModeChange: (Boolean) -> Unit = {}
) {
    NavHost(
        navController = navController,
        startDestination = NavigationDestinations.GOVERNORATE_LIST
    ) {
        // Home Screen: List of all governorates
        composable(NavigationDestinations.GOVERNORATE_LIST) {
            GovernorateListScreen(
                onGovernorateClick = { governorateId ->
                    navController.navigate("landmarks/$governorateId")
                },
                onProfileClick = {
                    navController.navigate(NavigationDestinations.PROFILE)
                }
            )
        }

        // Landmarks screen: List of landmarks for selected governorate
        composable(NavigationDestinations.LANDMARK_LIST) { backStackEntry ->
            val governorateId = backStackEntry.arguments?.getString("governorateId") ?: "cairo"

            LandmarksListScreen(
                governorateId = governorateId,
                onLandmarkClick = { landmarkId ->
                    navController.navigate("landmark/$landmarkId")
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // Profile Screen with Dark Mode toggle
        composable(NavigationDestinations.PROFILE) {
            Profile(
                onBackClick = {
                    navController.popBackStack()
                },
                onLogoutSuccess = {
                    navController.popBackStack()
                },
                onNavigateToSaved = { type ->
                    navController.navigate("saved_list/$type")
                },
                isDarkMode = isDarkMode,
                onDarkModeChange = onDarkModeChange
            )
        }

        // Saved Landmarks Screen
        composable(
            route = NavigationDestinations.SAVED_LIST,
            arguments = listOf(navArgument("type") { type = NavType.StringType })
        ) { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: "favorites"
            SavedLandmarksScreen(
                type = type,
                onBackClick = { navController.popBackStack() },
                onLandmarkClick = { landmarkId ->
                    navController.navigate("landmark/$landmarkId")
                }
            )
        }

        // Landmark detail screen
        composable(NavigationDestinations.LANDMARK_DETAIL) { backStackEntry ->
            val landmarkId = backStackEntry.arguments?.getString("landmarkId") ?: "0"

             LandmarkDetailScreen(
                 landmarkId = landmarkId.toInt(),
                 onBackClick = { navController.popBackStack() }
             )
        }
    }
}
