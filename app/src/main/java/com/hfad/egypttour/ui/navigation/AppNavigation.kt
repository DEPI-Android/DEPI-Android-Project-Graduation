package com.hfad.egypttour.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.hfad.egypttour.ui.screens.GovernorateListScreen
import com.hfad.egypttour.ui.screens.*
import com.hfad.egypttour.ui.screens.LandmarksListScreen
/**
 * Main navigation setup for Egypt Tour app
 *
 * Routes:
 * - governorates: Home screen with all governorates
 * - landmarks/{governorateId}: List of landmarks for a governorate
 * - landmark/{landmarkId}: Detail screen for a landmark (TODO)
 */
object NavigationDestinations {
    const val GOVERNORATE_LIST = "governorates"
    const val LANDMARK_LIST = "landmarks/{governorateId}"
    const val LANDMARK_DETAIL = "landmark/{landmarkId}"
    const val PROFILE = "profile"
}

@Composable
fun AppNavigation(navController: NavHostController) {
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
                // You must add this line to handle the back button click
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // Profile screen
        composable(NavigationDestinations.PROFILE) {
            Profile(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
//        // Landmark detail screen
        composable(NavigationDestinations.LANDMARK_DETAIL) { backStackEntry ->
            val landmarkId = backStackEntry.arguments?.getString("landmarkId") ?: "0"


             LandmarkDetailScreen(
                 landmarkId = landmarkId.toInt(),
                 onBackClick = { navController.popBackStack() }
             )
        }
    }
}
