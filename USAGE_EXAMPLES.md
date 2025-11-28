"""
BEST PRACTICES & CODE EXAMPLES FOR USING LandmarkListScreen
============================================================
"""

# EXAMPLE 1: Simple Display in MainActivity
# ==========================================

"""kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EgyptTourTheme {
                LandmarkListScreen(
                    governorateId = "cairo",
                    onLandmarkClick = { landmarkId ->
                        Log.d("MainActivity", "Landmark $landmarkId clicked")
                    }
                )
            }
        }
    }
}
"""

# EXAMPLE 2: Full App with Navigation
# ====================================

"""kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EgyptTourTheme {
                val navController = rememberNavController()
                
                NavHost(
                    navController = navController,
                    startDestination = "landmarks/cairo"
                ) {
                    composable("landmarks/{governorateId}") { backStackEntry ->
                        val governorateId = 
                            backStackEntry.arguments?.getString("governorateId") ?: "cairo"
                        
                        LandmarkListScreen(
                            governorateId = governorateId,
                            onLandmarkClick = { landmarkId ->
                                navController.navigate("detail/$landmarkId")
                            }
                        )
                    }
                    
                    composable("detail/{landmarkId}") { backStackEntry ->
                        val landmarkId = 
                            backStackEntry.arguments?.getString("landmarkId") ?: "0"
                        
                        LandmarkDetailScreen(
                            landmarkId = landmarkId.toInt(),
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
"""

# EXAMPLE 3: Switch Between Governorates with UI
# ===============================================

"""kotlin
@Composable
fun LandmarksWithGovernorateSelector() {
    var selectedGovernorate by remember { mutableStateOf("cairo") }
    
    Column(modifier = Modifier.fillMaxSize()) {
        // Governorate selector
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(listOf("cairo", "luxor", "aswan", "giza", "alexandria")) { id ->
                Button(
                    onClick = { selectedGovernorate = id },
                    modifier = Modifier.height(40.dp)
                ) {
                    Text(text = id.replaceFirstChar { it.uppercase() })
                }
            }
        }
        
        Divider()
        
        // Landmark list
        LandmarkListScreen(
            governorateId = selectedGovernorate,
            onLandmarkClick = { landmarkId ->
                Log.d("App", "Clicked landmark: $landmarkId")
            },
            modifier = Modifier.weight(1f)
        )
    }
}
"""

# EXAMPLE 4: With Error Handling & Retry Logic
# =============================================

"""kotlin
@Composable
fun LandmarksScreenWithErrorHandling() {
    val viewModel: LandmarkListViewModel = viewModel()
    val state by viewModel.landmarksState.collectAsState()
    
    when (state) {
        is Result.Loading -> {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
        
        is Result.Success -> {
            LandmarkListScreen(
                governorateId = "cairo",
                onLandmarkClick = { /* Navigate */ }
            )
        }
        
        is Result.Error -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Failed to load landmarks")
                Button(onClick = { viewModel.retry() }) {
                    Text("Retry")
                }
            }
        }
    }
}
"""

# EXAMPLE 5: Filterable Landmarks with Custom ViewModel
# ======================================================

"""kotlin
@Composable
fun AdvancedLandmarksScreen() {
    val viewModel: LandmarkListViewModel = viewModel()
    val landmarks by viewModel.filteredLandmarks.collectAsState()
    var hasMapFilter by remember { mutableStateOf(false) }
    
    Column(modifier = Modifier.fillMaxSize()) {
        // Advanced filters
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { hasMapFilter = !hasMapFilter },
                modifier = Modifier.weight(1f)
            ) {
                Text(if (hasMapFilter) "With Map" else "All")
            }
        }
        
        // Show filtered list
        LandmarkListScreen(
            governorateId = "cairo",
            onLandmarkClick = { landmarkId ->
                // Handle landmark click
            }
        )
    }
}
"""

# EXAMPLE 6: Tab-Based Navigation Between Governorates
# ====================================================

"""kotlin
@Composable
fun LandmarksWithTabNavigation() {
    val governorates = listOf("cairo", "luxor", "aswan", "giza", "alexandria")
    var selectedTabIndex by remember { mutableStateOf(0) }
    
    Column(modifier = Modifier.fillMaxSize()) {
        // Tab bar
        TabRow(selectedTabIndex = selectedTabIndex) {
            governorates.forEachIndexed { index, governorate ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(governorate.replaceFirstChar { it.uppercase() }) }
                )
            }
        }
        
        // Tab content
        LandmarkListScreen(
            governorateId = governorates[selectedTabIndex],
            onLandmarkClick = { landmarkId ->
                Log.d("Tabs", "Clicked: $landmarkId in ${governorates[selectedTabIndex]}")
            }
        )
    }
}
"""

# EXAMPLE 7: Search Across All Governorates
# ==========================================

"""kotlin
@Composable
fun GlobalSearchLandmarks() {
    var searchQuery by remember { mutableStateOf("") }
    var selectedGovernorate by remember { mutableStateOf("cairo") }
    
    val viewModel: LandmarkListViewModel = viewModel()
    
    Column(modifier = Modifier.fillMaxSize()) {
        // Global search
        TextField(
            value = searchQuery,
            onValueChange = { 
                searchQuery = it
                viewModel.updateSearchQuery(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("Search across all governorates...") }
        )
        
        // Governorate selector
        LazyRow(modifier = Modifier.padding(16.dp)) {
            items(listOf("cairo", "luxor", "aswan", "giza", "alexandria")) { gov ->
                FilterChip(
                    selected = selectedGovernorate == gov,
                    onClick = { selectedGovernorate = gov },
                    label = { Text(gov.replaceFirstChar { it.uppercase() }) }
                )
            }
        }
        
        // Results
        LandmarkListScreen(
            governorateId = selectedGovernorate,
            onLandmarkClick = { landmarkId ->
                Log.d("Search", "Result clicked: $landmarkId")
            }
        )
    }
}
"""

# EXAMPLE 8: Landscape Mode Optimization
# =======================================

"""kotlin
@Composable
fun ResponsiveLandmarksScreen() {
    val windowInfo = rememberWindowSizeClass()
    
    when (windowInfo.widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            // Phone portrait - single column
            LandmarkListScreen(
                governorateId = "cairo",
                onLandmarkClick = { /* Navigate to detail */ }
            )
        }
        
        WindowWidthSizeClass.Medium, WindowWidthSizeClass.Expanded -> {
            // Tablet or landscape - split view
            Row(modifier = Modifier.fillMaxSize()) {
                // Governorate list
                GovernorateListPanel(
                    modifier = Modifier.weight(1f),
                    onSelect = { /* Update state */ }
                )
                
                // Landmarks
                LandmarkListScreen(
                    governorateId = "cairo",
                    onLandmarkClick = { /* Show detail */ },
                    modifier = Modifier.weight(2f)
                )
            }
        }
    }
}
"""

# BEST PRACTICES
# ==============

"""
1. REUSE VIEWMODEL INSTANCES
   - Let Compose manage viewModel() unless you have specific needs
   - Don't create new instances on every recomposition
   
   ✓ Good:
   val vm: LandmarkListViewModel = viewModel()
   
   ✗ Bad:
   val vm = LandmarkListViewModel() // Creates new instance!

2. HANDLE BACK NAVIGATION
   - Use navController.popBackStack() when back is pressed
   - Preserve scroll position automatically (already built-in)
   
3. OPTIMIZE IMAGES
   - Coil handles caching automatically
   - Images are loaded asynchronously
   - Use ContentScale.Crop for thumbnails
   
4. SEARCH PERFORMANCE
   - Search is filtered in-memory (good for < 1000 items)
   - For larger datasets, move filtering to API
   
5. ACCESSIBILITY
   - All composables have contentDescription
   - Touch targets are minimum 48.dp
   - Colors have sufficient contrast
   
6. STATE MANAGEMENT
   - ScrollPosition is restored automatically
   - Use SavedStateHandle for process death recovery
   - ViewModel survives configuration changes

7. MEMORY OPTIMIZATION
   - LazyColumn only renders visible items
   - Images are recycled when off-screen
   - No memory leaks due to proper scope management

8. ERROR HANDLING
   - Retry button is shown on API errors
   - Network failures don't crash app
   - Empty states are user-friendly
"""

# DEBUGGING TIPS
# ==============

"""
Enable logging:
1. Add this to your ViewModel:
   private const val TAG = "LandmarkScreen"
   
2. Log important events:
   Log.d(TAG, "Loading landmarks for $governorate")
   Log.d(TAG, "Search results: ${filteredLandmarks.value.size} items")
   
3. Check in Logcat:
   adb logcat | grep LandmarkScreen
   
4. Enable network logging:
   - OkHttp logging-interceptor is included
   - See WikiApiService.kt for configuration
   
5. Profile performance:
   - Android Studio > Profiler
   - Check memory during scroll
   - Watch FPS on smooth animations
"""

