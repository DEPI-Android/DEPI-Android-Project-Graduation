package com.hfad.egypttour

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.hfad.egypttour.data.api.model.*

import com.hfad.egypttour.data.model.*
 import com.hfad.egypttour.data.repository.*
import com.hfad.egypttour.ui.theme.*
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
// --- TEMPORARY TEST CODE START ---
//            lifecycleScope.launch {
//
//                val apiService = RetrofitInstance.api
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

            }


            // --- TEMPORARY TEST CODE END ---

//            EgyptTourTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Greeting(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding)
//                    )
//                }
//            }
        }
    }


    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
//        EgyptTourTheme {
//            Greeting("Android")
//        }
    }