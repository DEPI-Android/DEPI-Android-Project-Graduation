package com.hfad.egypttour.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.hfad.egypttour.data.session.SessionManager
import com.hfad.egypttour.navigation.AppNavigation
import com.hfad.egypttour.ui.screens.GovernorateListScreen
import com.hfad.egypttour.ui.theme.EgyptTourTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Main activity for the app
 * Manages dark mode state and provides it to the theme
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Read dark mode preference and make it reactive
            var isDarkMode by remember { mutableStateOf(sessionManager.isDarkMode()) }
            
            EgyptTourTheme(darkTheme = isDarkMode) {
                val navController = rememberNavController()
                AppNavigation(
                    navController = navController,
                    isDarkMode = isDarkMode,
                    onDarkModeChange = { enabled ->
                        sessionManager.setDarkMode(enabled)
                        isDarkMode = enabled
                    }
                )
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        EgyptTourTheme {
            GovernorateListScreen(onGovernorateClick = { }, onProfileClick = { })
        }
    }
}