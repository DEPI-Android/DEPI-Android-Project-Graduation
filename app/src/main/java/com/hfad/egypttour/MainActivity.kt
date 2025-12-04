package com.hfad.egypttour

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.hfad.egypttour.ui.navigation.AppNavigation
import com.hfad.egypttour.ui.screens.*
import com.hfad.egypttour.ui.theme.EgyptTourTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EgyptTourTheme {
                // Use navigation with NavController
                val navController = rememberNavController()
                AppNavigation(navController = navController)
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        EgyptTourTheme {
             GovernorateListScreen(onGovernorateClick = {  }, onProfileClick = {  })
        }
    }
}