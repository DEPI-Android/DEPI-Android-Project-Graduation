//package com.hfad.egypttour
//
//import android.annotation.SuppressLint
//import android.os.Bundle
//import android.util.Log
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.enableEdgeToEdge
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.lifecycle.lifecycleScope
//import com.hfad.egypttour.data.api.RetrofitInstance
//import com.hfad.egypttour.data.api.WikiApiService
//import com.hfad.egypttour.data.model.Governorate
//import com.hfad.egypttour.data.model.Result
//import com.hfad.egypttour.data.repository.LandmarkRepository
//import com.hfad.egypttour.ui.theme.EgyptTourTheme
//import kotlinx.coroutines.launch
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//
//class MainActivity : ComponentActivity() {
//    @SuppressLint("CoroutineCreationDuringComposition")
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContent {
//// --- TEMPORARY TEST CODE START ---
//            lifecycleScope.launch {
//
//                val apiService = RetrofitInstance.RetrofitInstance.api
//
//                // 2. Initialize Repository
//                val repository = LandmarkRepository(apiService)
//
//                // 3. Test a specific Governorate (e.g., Cairo or Faiyum)
//                // Ensure your Governorate Enum has a valid .wikiCategory set!
//                val testGovernorate = Governorate.CAIRO
//
//                Log.i("TEST_RUNNER", "Calling getLandmarks for ${testGovernorate.displayName}...")
//
//                val result = repository.getLandmarks(testGovernorate)
//
//                // 4. Check results
//                when (result) {
//                    is Result.Success -> {
//                        Log.i("TEST_RUNNER", "SUCCESS! Found ${result.data.size} items.")
//                        result.data.forEach { landmark ->
//                            Log.d(
//                                "TEST_RUNNER",
//                                "Item: ${landmark.name} | Img: ${landmark.imageUrl}\n" +
//                                        " Desc: ${landmark.description} | Coords: ${landmark.lat}, ${landmark.lon}"
//                             )
//                        }
//                    }
//
//                    is Result.Error -> {
//                        Log.e("TEST_RUNNER", "FAILURE: ${result.massage}")
//                        result.exception?.printStackTrace()
//                    }
//
//                    else -> {}
//                }
//
//            }
//
//
//            // --- TEMPORARY TEST CODE END ---
//
//            EgyptTourTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Greeting(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding)
//                    )
//                }
//            }
//        }
//    }
//
//
//    @Composable
//    fun Greeting(name: String, modifier: Modifier = Modifier) {
//        Text(
//            text = "Hello $name!",
//            modifier = modifier
//        )
//    }
//
//    @Preview(showBackground = true)
//    @Composable
//    fun GreetingPreview() {
//        EgyptTourTheme {
//            Greeting("Android")
//        }
//    }}
package com.hfad.egypttour

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hfad.egypttour.ui.theme.EgyptTourTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EgyptTourTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {

        // SCREEN 1: Home (List of Cities)
        composable("home") {
            GovernorateListScreen(
                onGovernorateClick = { cityId ->
                    // Navigate to details, passing the ID (e.g., "luxor")
                    navController.navigate("landmarks/$cityId")
                }
            )
        }

        // SCREEN 2: Landmarks (Dynamic List)
        composable(
            route = "landmarks/{cityId}",
            arguments = listOf(navArgument("cityId") { type = NavType.StringType })
        ) { backStackEntry ->
            // 1. Get the ID from the navigation URL (e.g., "luxor")
            val cityId = backStackEntry.arguments?.getString("cityId") ?: "cairo"

            // 2. Get the ViewModel
            val viewModel: LandmarkViewModel = viewModel()

            // 3. Trigger the data fetch whenever the cityId changes
            LaunchedEffect(cityId) {
                viewModel.loadLandmarks(cityId)
            }

            // 4. Observe the data
            val landmarks by viewModel.landmarks.collectAsState()
            val isLoading by viewModel.isLoading.collectAsState()

            // 5. Show the Screen (Pass the real data!)
            LandmarksScreen(
                cityName = cityId.uppercase(),
                landmarks = landmarks,
                isLoading = isLoading,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}