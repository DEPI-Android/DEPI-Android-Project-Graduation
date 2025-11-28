//////package com.hfad.egypttour.ui.screens
//////
//////
//////
//////import androidx.compose.foundation.background
//////import androidx.compose.foundation.clickable
//////import androidx.compose.foundation.layout.*
//////import androidx.compose.foundation.lazy.LazyColumn
//////import androidx.compose.foundation.lazy.LazyListState
//////import androidx.compose.foundation.lazy.items
//////import androidx.compose.foundation.lazy.rememberLazyListState
//////import androidx.compose.foundation.shape.RoundedCornerShape
//////import androidx.compose.material.icons.Icons
//////
//////import androidx.compose.material.icons.filled.Clear
//////import androidx.compose.material.icons.filled.*
//////import androidx.compose.material.icons.filled.Search
//////import androidx.compose.material3.*
//////import androidx.compose.runtime.*
//////import kotlinx.coroutines.flow.distinctUntilChanged
//////import androidx.compose.runtime.snapshotFlow
//////import androidx.compose.ui.Alignment
//////import androidx.compose.ui.Modifier
//////import androidx.compose.ui.graphics.Color
//////import androidx.compose.ui.layout.ContentScale
//////import androidx.compose.ui.unit.dp
//////import androidx.compose.ui.unit.sp
//////import androidx.lifecycle.viewmodel.compose.viewModel
//////import coil.compose.AsyncImage
//////import com.hfad.egypttour.data.model.Governorate
//////import com.hfad.egypttour.data.model.LandMark
////// import com.hfad.egypttour.data.model.Result
//////import com.hfad.egypttour.ui.viewmodel.LandmarkListViewModel
//////
//////@OptIn(ExperimentalMaterial3Api::class)
//////@Composable
//////fun LandmarkListScreen(
//////    governorateId: String,
//////    onLandmarkClick: (Int) -> Unit,
//////    viewModel: LandmarkListViewModel = viewModel()
//////) {
//////    val governorate = remember(governorateId) {
//////        Governorate.fromId(governorateId)
//////    }
//////
//////    if (governorate == null) {
//////        ErrorScreen("Invalid governorate")
//////        return
//////    }
//////
//////    // Load landmarks when screen opens
//////    LaunchedEffect(governorate) {
//////        viewModel.loadLandmarks(governorate)
//////        viewModel.resetScrollPosition()  // NEW: Reset scroll when switching governorates
//////    }
//////
//////    val state by viewModel.landmarksState.collectAsState()
//////    val searchQuery by viewModel.searchQuery.collectAsState()
//////    val filteredLandmarks by viewModel.filteredLandmarks.collectAsState()
//////    val scrollPosition by viewModel.scrollPosition.collectAsState()  // NEW
//////
//////    // NEW: Scroll state restoration
//////    val listState = rememberLazyListState(initialFirstVisibleItemIndex = scrollPosition)
//////
//////    // NEW: Save scroll position when it changes - use snapshotFlow to avoid composition reading issue
//////    LaunchedEffect(listState) {
//////        snapshotFlow { listState.firstVisibleItemIndex }
//////            .distinctUntilChanged()
//////            .collect { position ->
//////                viewModel.saveScrollPosition(position)
//////            }
//////    }
//////
//////    Scaffold(
//////        topBar = {
//////            Column {
//////                TopAppBar(
//////                    title = { Text(governorate.displayName) }
//////                )
//////
//////                if (state is Result.Success) {
//////                    SearchBar(
//////                        query = searchQuery,
//////                        onQueryChange = { viewModel.updateSearchQuery(it) },
//////                        onClearClick = { viewModel.clearSearch() }
//////                    )
//////                }
//////            }
//////        }
//////    ) { padding ->
//////        Box(
//////            modifier = Modifier
//////                .fillMaxSize()
//////                .padding(padding)
//////        ) {
//////            when (state) {
//////                is Result.Loading -> {
//////                    LoadingScreen()
//////                }
//////
//////                is Result.Success -> {
//////                    if (filteredLandmarks.isEmpty()) {
//////                        EmptyScreen(
//////                            message = if (searchQuery.isBlank()) {
//////                                "No landmarks found"
//////                            } else {
//////                                "No results for '$searchQuery'"
//////                            }
//////                        )
//////                    } else {
//////                        LandmarkList(
//////                            landmarks = filteredLandmarks,
//////                            listState = listState,  // NEW: Pass scroll state
//////                            onLandmarkClick = onLandmarkClick
//////                        )
//////                    }
//////                }
//////
//////                is Result.Error -> {
//////                    ErrorScreen(
//////                        message = viewModel.errorMessage ?: "Unknown error",
//////                        onRetryClick = { viewModel.retry() }
//////                    )
//////                }
//////            }
//////        }
//////    }
//////}
//////
//////@Composable
//////fun SearchBar(
//////    query: String,
//////    onQueryChange: (String) -> Unit,
//////    onClearClick: () -> Unit
//////) {
//////    TextField(
//////        value = query,
//////        onValueChange = onQueryChange,
//////        modifier = Modifier
//////            .fillMaxWidth()
//////            .padding(horizontal = 16.dp, vertical = 8.dp),
//////        placeholder = { Text("Search landmarks...") },
//////        leadingIcon = { Icon(Icons.Default.Search, "Search") },
//////        trailingIcon = {
//////            if (query.isNotBlank()) {
//////                IconButton(onClick = onClearClick) {
//////                    Icon(Icons.Default.Clear, "Clear")
//////                }
//////            }
//////        },
//////        singleLine = true
//////    )
//////}
//////
///////**
////// * UPDATED: Now accepts LazyListState for scroll restoration
////// */
//////@Composable
//////fun LandmarkList(
//////    landmarks: List<LandMark>,
//////    listState: LazyListState,  // NEW: Scroll state parameter
//////    onLandmarkClick: (Int) -> Unit
//////) {
//////    LazyColumn(
//////        state = listState,  // NEW: Use provided scroll state
//////        contentPadding = PaddingValues(16.dp),
//////        verticalArrangement = Arrangement.spacedBy(12.dp)
//////    ) {
//////        items(landmarks) { landmark ->
//////            LandmarkCard(
//////                landmark = landmark,
//////                onClick = { onLandmarkClick(landmark.id) }
//////            )
//////        }
//////    }
//////}
//////
///////**
////// * UPDATED: Now shows gallery indicator for multi-photo landmarks
////// */
//////@Composable
//////fun LandmarkCard(
//////    landmark: LandMark,
//////    onClick: () -> Unit
//////) {
//////    var isLoading by remember { mutableStateOf(true) }
//////    var hasError by remember { mutableStateOf(false) }
//////
//////    Card(
//////        modifier = Modifier
//////            .fillMaxWidth()
//////            .clickable(onClick = onClick)
//////    ) {
//////        Row(modifier = Modifier.height(120.dp)) {
//////            Box(modifier = Modifier.width(120.dp)) {
//////                AsyncImage(
//////                    model = landmark.imageUrl,
//////                    contentDescription = landmark.name,
//////                    modifier = Modifier.fillMaxSize(),
//////                    contentScale = ContentScale.Crop,
//////                    alignment = Alignment.Center,
//////                    onState = { state ->
//////                        isLoading = state is coil.compose.AsyncImagePainter.State.Loading
//////                        hasError = state is coil.compose.AsyncImagePainter.State.Error
//////                    }
//////                )
//////
//////                // Loading indicator
//////                if (isLoading) {
//////                    Box(
//////                        modifier = Modifier
//////                            .fillMaxSize()
//////                            .background(Color.Gray.copy(alpha = 0.3f)),
//////                        contentAlignment = Alignment.Center
//////                    ) {
//////                        CircularProgressIndicator(
//////                            modifier = Modifier.size(30.dp)
//////                        )
//////                    }
//////                }
//////
//////                // Error placeholder
//////                if (hasError) {
//////                    Box(
//////                        modifier = Modifier
//////                            .fillMaxSize()
//////                            .background(Color.Gray.copy(alpha = 0.5f)),
//////                        contentAlignment = Alignment.Center
//////                    ) {
//////                        Icon(
//////                            imageVector = Icons.Default.Image,
//////                            contentDescription = null,
//////                            tint = Color.White.copy(alpha = 0.5f),
//////                            modifier = Modifier.size(30.dp)
//////                        )
//////                    }
//////                }
//////
//////                // NEW: Gallery indicator badge
//////                if (landmark.hasGallery) {
//////                    Surface(
//////                        modifier = Modifier
//////                            .align(Alignment.TopEnd)
//////                            .padding(4.dp),
//////                        shape = RoundedCornerShape(4.dp),
//////                        color = Color.Black.copy(alpha = 0.7f)
//////                    ) {
//////                        Row(
//////                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
//////                            verticalAlignment = Alignment.CenterVertically,
//////                            horizontalArrangement = Arrangement.spacedBy(3.dp)
//////                        ) {
//////                            Icon(
//////                                imageVector = Icons.Default.PhotoLibrary,
//////                                contentDescription = null,
//////                                tint = Color.White,
//////                                modifier = Modifier.size(14.dp)
//////                            )
//////                            Text(
//////                                text = "${landmark.totalImages}",
//////                                color = Color.White,
//////                                fontSize = 11.sp
//////                            )
//////                        }
//////                    }
//////                }
//////            }
//////
//////            Column(
//////                modifier = Modifier
//////                    .fillMaxSize()
//////                    .padding(12.dp)
//////            ) {
//////                Text(
//////                    text = landmark.name,
//////                    style = MaterialTheme.typography.titleMedium,
//////                    maxLines = 2  // NEW: Prevent overflow for long names
//////                )
//////
//////                Spacer(modifier = Modifier.height(4.dp))
//////
//////                Text(
//////                    text = landmark.shortDescription,
//////                    style = MaterialTheme.typography.bodySmall,
//////                    maxLines = 3
//////                )
//////
//////                if (landmark.haseCoordinates) {
//////                    Spacer(modifier = Modifier.weight(1f))
//////                    Text(
//////                        text = "📍 View on map",
//////                        style = MaterialTheme.typography.labelSmall,
//////                        color = MaterialTheme.colorScheme.primary
//////                    )
//////                }
//////            }
//////        }
//////    }
//////}
//////
//////@Composable
//////fun LoadingScreen() {
//////    Box(modifier = Modifier.fillMaxSize()) {
//////        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
//////    }
//////}
//////
//////@Composable
//////fun ErrorScreen(message: String, onRetryClick: (() -> Unit)? = null) {
//////    Column(
//////        modifier = Modifier
//////            .fillMaxSize()
//////            .padding(16.dp),
//////        horizontalAlignment = Alignment.CenterHorizontally,
//////        verticalArrangement = Arrangement.Center
//////    ) {
//////        Text(
//////            text = "⚠️ Error",
//////            style = MaterialTheme.typography.headlineMedium
//////        )
//////        Spacer(modifier = Modifier.height(8.dp))
//////        Text(
//////            text = message,
//////            style = MaterialTheme.typography.bodyMedium
//////        )
//////        if (onRetryClick != null) {
//////            Spacer(modifier = Modifier.height(16.dp))
//////            Button(onClick = onRetryClick) {
//////                Text("Retry")
//////            }
//////        }
//////    }
//////}
//////@Composable
//////fun EmptyScreen(message: String) {
//////    Box(modifier = Modifier.fillMaxSize()) {
//////        Text(
//////            text = message,
//////            modifier = Modifier.align(Alignment.Center),
//////            style = MaterialTheme.typography.bodyLarge
//////        )
//////    }
//////}
////
////
//
//
//package com.hfad.egypttour.ui.screens
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.LazyListState
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.lazy.rememberLazyListState
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material.icons.filled.Clear
//import androidx.compose.material.icons.filled.Image
//import androidx.compose.material.icons.filled.PhotoLibrary
//import androidx.compose.material.icons.filled.Search
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import kotlinx.coroutines.flow.distinctUntilChanged
//import androidx.compose.runtime.snapshotFlow
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import coil.compose.AsyncImage
//import com.hfad.egypttour.data.model.Governorate
//import com.hfad.egypttour.data.model.LandMark
//import com.hfad.egypttour.data.model.Result
//import com.hfad.egypttour.ui.viewmodel.LandmarkListViewModel
//
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun LandmarkListScreen(
//    governorateId: String,
//    onLandmarkClick: (Int) -> Unit,
//    onBackClick: () -> Unit,
//    viewModel: LandmarkListViewModel = viewModel()
//) {
//    val governorate = remember(governorateId) {
//        Governorate.fromId(governorateId)
//    }
//
//    if (governorate == null) {
//        ErrorScreen("Invalid governorate")
//        return
//    }
//
//    // Load landmarks when screen opens
//    LaunchedEffect(governorate) {
//        viewModel.loadLandmarks(governorate)
//        viewModel.resetScrollPosition()
//    }
//
//    val state by viewModel.landmarksState.collectAsState()
//    val searchQuery by viewModel.searchQuery.collectAsState()
//    val filteredLandmarks by viewModel.filteredLandmarks.collectAsState()
//    val scrollPosition by viewModel.scrollPosition.collectAsState()
//
//    val listState = rememberLazyListState(initialFirstVisibleItemIndex = scrollPosition)
//
//    // Save scroll position when it changes
//    LaunchedEffect(listState) {
//        snapshotFlow { listState.firstVisibleItemIndex }
//            .distinctUntilChanged()
//            .collect { position ->
//                viewModel.saveScrollPosition(position)
//            }
//    }
//
//    Scaffold(
//        topBar = {
//            Column {
//                TopAppBar(
//                    title = { Text(governorate.displayName) },
//                    navigationIcon = { // Added Navigation Icon
//                        IconButton(onClick = onBackClick) {
//                            Icon(
//                                imageVector = Icons.Default.ArrowBack,
//                                contentDescription = "Back"
//                            )
//                        }
//                    }
//                )
//
//                if (state is Result.Success) {
//                    SearchBar(
//                        query = searchQuery,
//                        onQueryChange = { viewModel.updateSearchQuery(it) },
//                        onClearClick = { viewModel.clearSearch() }
//                    )
//                }
//            }
//        }
//    ) { padding ->
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(padding)
//        ) {
//            when (state) {
//                is Result.Loading -> {
//                    LoadingScreen()
//                }
//
//                is Result.Success -> {
//                    if (filteredLandmarks.isEmpty()) {
//                        EmptyScreen(
//                            message = if (searchQuery.isBlank()) {
//                                "No landmarks found"
//                            } else {
//                                "No results for '$searchQuery'"
//                            }
//                        )
//                    } else {
//                        LandmarkList(
//                            landmarks = filteredLandmarks,
//                            listState = listState,
//                            onLandmarkClick = onLandmarkClick
//                        )
//                    }
//                }
//
//                is Result.Error -> {
//                    ErrorScreen(
//                        message = viewModel.errorMessage ?: "Unknown error",
//                        onRetryClick = { viewModel.retry() }
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun SearchBar(
//    query: String,
//    onQueryChange: (String) -> Unit,
//    onClearClick: () -> Unit
//) {
//    TextField(
//        value = query,
//        onValueChange = onQueryChange,
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 16.dp, vertical = 8.dp),
//        placeholder = { Text("Search landmarks...") },
//        leadingIcon = { Icon(Icons.Default.Search, "Search") },
//        trailingIcon = {
//            if (query.isNotBlank()) {
//                IconButton(onClick = onClearClick) {
//                    Icon(Icons.Default.Clear, "Clear")
//                }
//            }
//        },
//        singleLine = true
//    )
//}
//
//@Composable
//fun LandmarkList(
//    landmarks: List<LandMark>,
//    listState: LazyListState,
//    onLandmarkClick: (Int) -> Unit
//) {
//    LazyColumn(
//        state = listState,
//        contentPadding = PaddingValues(16.dp),
//        verticalArrangement = Arrangement.spacedBy(12.dp)
//    ) {
//        items(landmarks) { landmark ->
//            LandmarkCard(
//                landmark = landmark,
//                onClick = { onLandmarkClick(landmark.id) }
//            )
//        }
//    }
//}
//
//@Composable
//fun LandmarkCard(
//    landmark: LandMark,
//    onClick: () -> Unit
//) {
//    var isLoading by remember { mutableStateOf(true) }
//    var hasError by remember { mutableStateOf(false) }
//
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable(onClick = onClick)
//    ) {
//        Row(modifier = Modifier.height(120.dp)) {
//            Box(modifier = Modifier.width(120.dp)) {
//                AsyncImage(
//                    model = landmark.imageUrl,
//                    contentDescription = landmark.name,
//                    modifier = Modifier.fillMaxSize(),
//                    contentScale = ContentScale.Crop,
//                    alignment = Alignment.Center,
//                    onState = { state ->
//                        isLoading = state is coil.compose.AsyncImagePainter.State.Loading
//                        hasError = state is coil.compose.AsyncImagePainter.State.Error
//                    }
//                )
//
//                if (isLoading) {
//                    Box(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .background(Color.Gray.copy(alpha = 0.3f)),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        CircularProgressIndicator(
//                            modifier = Modifier.size(30.dp)
//                        )
//                    }
//                }
//
//                if (hasError) {
//                    Box(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .background(Color.Gray.copy(alpha = 0.5f)),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Icon(
//                            imageVector = Icons.Default.Image,
//                            contentDescription = null,
//                            tint = Color.White.copy(alpha = 0.5f),
//                            modifier = Modifier.size(30.dp)
//                        )
//                    }
//                }
//
//                if (landmark.hasGallery) {
//                    Surface(
//                        modifier = Modifier
//                            .align(Alignment.TopEnd)
//                            .padding(4.dp),
//                        shape = RoundedCornerShape(4.dp),
//                        color = Color.Black.copy(alpha = 0.7f)
//                    ) {
//                        Row(
//                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
//                            verticalAlignment = Alignment.CenterVertically,
//                            horizontalArrangement = Arrangement.spacedBy(3.dp)
//                        ) {
//                            Icon(
//                                imageVector = Icons.Default.PhotoLibrary,
//                                contentDescription = null,
//                                tint = Color.White,
//                                modifier = Modifier.size(14.dp)
//                            )
//                            Text(
//                                text = "${landmark.totalImages}",
//                                color = Color.White,
//                                fontSize = 11.sp
//                            )
//                        }
//                    }
//                }
//            }
//
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(12.dp)
//            ) {
//                Text(
//                    text = landmark.name,
//                    style = MaterialTheme.typography.titleMedium,
//                    maxLines = 2
//                )
//
//                Spacer(modifier = Modifier.height(4.dp))
//
//                Text(
//                    text = landmark.shortDescription,
//                    style = MaterialTheme.typography.bodySmall,
//                    maxLines = 3
//                )
//
//                if (landmark.haseCoordinates) {
//                    Spacer(modifier = Modifier.weight(1f))
//                    Text(
//                        text = "📍 View on map",
//                        style = MaterialTheme.typography.labelSmall,
//                        color = MaterialTheme.colorScheme.primary
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun LoadingScreen() {
//    Box(modifier = Modifier.fillMaxSize()) {
//        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
//    }
//}
//
//@Composable
//fun ErrorScreen(message: String, onRetryClick: (() -> Unit)? = null) {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        Text(
//            text = "⚠️ Error",
//            style = MaterialTheme.typography.headlineMedium
//        )
//        Spacer(modifier = Modifier.height(8.dp))
//        Text(
//            text = message,
//            style = MaterialTheme.typography.bodyMedium
//        )
//        if (onRetryClick != null) {
//            Spacer(modifier = Modifier.height(16.dp))
//            Button(onClick = onRetryClick) {
//                Text("Retry")
//            }
//        }
//    }
//}
//
//@Composable
//fun EmptyScreen(message: String) {
//    Box(modifier = Modifier.fillMaxSize()) {
//        Text(
//            text = message,
//            modifier = Modifier.align(Alignment.Center),
//            style = MaterialTheme.typography.bodyLarge
//        )
//    }
//}
//
//
//
//
//
////
////package com.hfad.egypttour.ui.screens
////
////import androidx.lifecycle.ViewModel
////import androidx.lifecycle.viewModelScope
////import com.hfad.egypttour.data.api.RetrofitInstance
////import com.hfad.egypttour.data.model.Governorate
////import com.hfad.egypttour.data.model.LandMark
////import com.hfad.egypttour.data.model.Result
////import com.hfad.egypttour.data.repository.LandmarkRepository
////import kotlinx.coroutines.flow.MutableStateFlow
////import kotlinx.coroutines.flow.SharingStarted
////import kotlinx.coroutines.flow.StateFlow
////import kotlinx.coroutines.flow.asStateFlow
////import kotlinx.coroutines.flow.combine
////import kotlinx.coroutines.flow.stateIn
////import kotlinx.coroutines.launch
////
////// FIX: No arguments in the constructor!
////// This allows the default 'viewModel()' factory to create it without Hilt.
////class LandmarkListViewModel : ViewModel() {
////
////    // State for the raw data from Repository
////    private val _landmarksState = MutableStateFlow<Result<List<LandMark>>>(Result.Loading)
////    val landmarksState = _landmarksState.asStateFlow()
////
////    // State for Search Bar
////    private val _searchQuery = MutableStateFlow("")
////    val searchQuery = _searchQuery.asStateFlow()
////
////    // State for Scroll Position
////    private val _scrollPosition = MutableStateFlow(0)
////    val scrollPosition = _scrollPosition.asStateFlow()
////
////    // Error message holder
////    var errorMessage: String? = null
////        private set
////
////    // FILTERING LOGIC: Combines the Data and the Search Query automatically
////    val filteredLandmarks: StateFlow<List<LandMark>> = combine(
////        _landmarksState,
////        _searchQuery
////    ) { result, query ->
////        if (result is Result.Success) {
////            if (query.isBlank()) {
////                result.data
////            } else {
////                result.data.filter {
////                    it.name.contains(query, ignoreCase = true) ||
////                            it.shortDescription.contains(query, ignoreCase = true)
////                }
////            }
////        } else {
////            emptyList()
////        }
////    }.stateIn(
////        viewModelScope,
////        SharingStarted.WhileSubscribed(5000),
////        emptyList()
////    )
////
////    fun loadLandmarks(governorate: Governorate) {
////        viewModelScope.launch {
////            _landmarksState.value = Result.Loading
////
////            // Call the Singleton Repository directly
////            val result = LandmarkRepository(apiService = RetrofitInstance.api).getLandmarks(governorate)
////
////            if (result is Result.Error) {
////                errorMessage = result.massage
////            }
////            _landmarksState.value = result
////        }
////    }
////
////    fun updateSearchQuery(query: String) {
////        _searchQuery.value = query
////    }
////
////    fun clearSearch() {
////        _searchQuery.value = ""
////    }
////
////    fun saveScrollPosition(position: Int) {
////        _scrollPosition.value = position
////    }
////
////    fun resetScrollPosition() {
////        _scrollPosition.value = 0
////    }
////
////    fun retry() {
////        // Reload the last loaded data (logic simplification for MVP)
////        // In a real app, you'd store the last requested governorate
////    }
////}


//package com.hfad.egypttour.ui.screens
//
//import android.util.Log
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.grid.GridCells
//import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
//import androidx.compose.foundation.lazy.grid.items
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowBack
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material.icons.filled.Clear
//import androidx.compose.material.icons.filled.Home
//import androidx.compose.material.icons.filled.Place
//import androidx.compose.material.icons.filled.Search
//import androidx.compose.material.icons.filled.Warning
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.vector.rememberVectorPainter
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import coil.compose.AsyncImage
//import coil.request.ImageRequest
//import com.hfad.egypttour.data.model.LandMark
//import com.hfad.egypttour.ui.theme.*
//
//// --- MAIN SCREEN ---
//@Composable
//fun LandmarksListScreen(
//    cityName: String,
//    landmarks: List<LandMark>,
//    isLoading: Boolean,
//    onBackClick: () -> Unit
//) {
//    var query by remember { mutableStateOf("") }
//
//    val filteredLandmarks = landmarks.filter {
//        it.name.contains(query, ignoreCase = true)
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(MaterialTheme.colorScheme.background)
//    ) {
//        // --- HEADER ---
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .background(EgyptGold)
//                .padding(top = 48.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            IconButton(onClick = onBackClick) {
//                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextWhite, modifier = Modifier.size(28.dp))
//            }
//            Text(
//                text = cityName,
//                style = CityTitleTextStyle,
//                color = TextWhite
//            )
//            IconButton(onClick = { /* Handle Home */ }) {
//                Icon(Icons.Default.Home, contentDescription = "Home", tint = TextWhite, modifier = Modifier.size(28.dp))
//            }
//        }
//
//        // --- SEARCH BAR ---
//        Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
//            OutlinedTextField(
//                value = query,
//                onValueChange = { query = it },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .background(Color.White, RoundedCornerShape(12.dp)),
//                placeholder = { Text("Search for Landmarks", style = SearchTextStyle) },
//                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextGray) },
//                trailingIcon = if (query.isNotEmpty()) {
//                    { IconButton(onClick = { query = "" }) { Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextGray) } }
//                } else null,
//                shape = RoundedCornerShape(12.dp),
//                colors = OutlinedTextFieldDefaults.colors(
//                    focusedBorderColor = EgyptGold,
//                    unfocusedBorderColor = EgyptGold,
//                    focusedContainerColor = Color.White,
//                    unfocusedContainerColor = Color.White,
//                    cursorColor = EgyptGold,
//                    focusedTextColor = TextBlack,
//                    unfocusedTextColor = TextBlack
//                ),
//                singleLine = true
//            )
//        }
//
//        // --- CONTENT AREA ---
//        Box(
//            modifier = Modifier.fillMaxWidth().weight(1f),
//            contentAlignment = Alignment.Center
//        ) {
//            if (isLoading) {
//                CircularProgressIndicator(color = EgyptGold)
//            } else {
//                if (filteredLandmarks.isEmpty()) {
//                    Text("No landmarks found", color = TextGray)
//                } else {
//                    LazyVerticalGrid(
//                        columns = GridCells.Fixed(2),
//                        contentPadding = PaddingValues(16.dp),
//                        horizontalArrangement = Arrangement.spacedBy(16.dp),
//                        verticalArrangement = Arrangement.spacedBy(24.dp),
//                        modifier = Modifier.fillMaxSize()
//                    ) {
//                        items(filteredLandmarks) { landmark ->
//                            LandmarkItem(landmark)
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun LandmarkItem(landmark: LandMark) {
//    Column(
//        horizontalAlignment = Alignment.CenterHorizontally,
//        modifier = Modifier.fillMaxWidth()
//    ) {
//        // Image Card
//        Card(
//            shape = RoundedCornerShape(8.dp),
//            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
//            modifier = Modifier.fillMaxWidth().height(128.dp)
//        ) {
//            if (landmark.imageUrl.isNullOrEmpty()) {
//                // CASE 1: No Image URL -> Show Gray Placeholder
//                Box(
//                    modifier = Modifier.fillMaxSize().background(Color.LightGray),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Icon(Icons.Default.Place, contentDescription = null, tint = Color.White, modifier = Modifier.size(48.dp))
//                }
//            } else {
//                // CASE 2: Valid Image URL -> Load with Coil
//                AsyncImage(
//                    model = ImageRequest.Builder(LocalContext.current)
//                        .data(landmark.imageUrl)
//                        // FIX: Add User-Agent header so Wikipedia doesn't block the image request
//                        .addHeader("User-Agent", "EgyptTourApp/1.0")
//                        .crossfade(true)
//                        .listener(
//                            onError = { _, result ->
//                                // This prints the EXACT error to your Logcat so we know why it fails
//                                Log.e("CoilError", "Failed to load ${landmark.name}: ${result.throwable.message}")
//                            }
//                        )
//                        .build(),
//                    contentDescription = landmark.name,
//                    contentScale = ContentScale.Crop,
//                    modifier = Modifier.fillMaxSize(),
//                    // While loading, show the pin icon
//                    placeholder = rememberVectorPainter(Icons.Default.Place),
//                    // If loading fails, show a warning icon (Red)
//                    error = rememberVectorPainter(Icons.Default.Warning)
//                )
//            }
//        }
//
//        // Floating Label
//        Surface(
//            color = EgyptLightGold,
//            shape = RoundedCornerShape(4.dp),
//            shadowElevation = 2.dp,
//            modifier = Modifier.widthIn(min = 100.dp, max = 160.dp).offset(y = (-12).dp)
//        ) {
//            Text(
//                text = landmark.name,
//                style =     LandmarkLabelTextStyle,
//                textAlign = TextAlign.Center,
//                maxLines = 1,
//                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
//            )
//        }
//    }
//}


package com.hfad.egypttour.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.hfad.egypttour.data.model.Governorate
import com.hfad.egypttour.data.model.LandMark
import com.hfad.egypttour.data.model.Result
import com.hfad.egypttour.ui.theme.*
import com.hfad.egypttour.ui.viewmodel.LandmarkListViewModel

/**
 * FIXED: Now accepts governorateId from navigation and loads data via ViewModel
 */
@Composable
fun LandmarksListScreen(
    governorateId: String,
    onLandmarkClick: (Int) -> Unit,
    onBackClick: () -> Unit,
    viewModel: LandmarkListViewModel = viewModel()
) {
    // Convert governorate ID to Governorate enum
    val governorate = remember(governorateId) {
        Governorate.fromId(governorateId)
    }

    // Handle invalid governorate ID
    if (governorate == null) {
        ErrorScreen(
            message = "Invalid governorate: $governorateId",
            onBackClick = onBackClick
        )
        return
    }

    // Load landmarks when screen opens
    LaunchedEffect(governorate) {
        viewModel.loadLandmarks(governorate)
        viewModel.resetScrollPosition()
    }

    // Observe ViewModel states
    val state by viewModel.landmarksState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filteredLandmarks by viewModel.filteredLandmarks.collectAsState()

    // Render UI based on state
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // --- HEADER ---
        LandmarkHeader(
            cityName = governorate.displayName,
            onBackClick = onBackClick
        )

        // --- SEARCH BAR ---
        SearchBar(
            query = searchQuery,
            onQueryChange = { viewModel.updateSearchQuery(it) },
            onClearClick = { viewModel.clearSearch() }
        )

        // --- CONTENT AREA (State-based rendering) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            when (state) {
                is Result.Loading -> {
                    CircularProgressIndicator(color = EgyptGold)
                }

                is Result.Success -> {
                    if (filteredLandmarks.isEmpty()) {
                        EmptyState(
                            message = if (searchQuery.isBlank()) {
                                "No landmarks found in ${governorate.displayName}"
                            } else {
                                "No results for '$searchQuery'"
                            }
                        )
                    } else {
                        LandmarkGrid(
                            landmarks = filteredLandmarks,
                            onLandmarkClick = onLandmarkClick
                        )
                    }
                }

                is Result.Error -> {
                    ErrorState(
                        message = viewModel.errorMessage ?: "Failed to load landmarks",
                        onRetryClick = { viewModel.retry() }
                    )
                }
            }
        }
    }
}

// --- HEADER COMPONENT ---
@Composable
private fun LandmarkHeader(
    cityName: String,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(EgyptGold)
            .padding(top = 48.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = TextWhite,
                modifier = Modifier.size(28.dp)
            )
        }
        Text(
            text = cityName,
            style = CityTitleTextStyle,
            color = TextWhite
        )
        // Placeholder for symmetry
        Box(modifier = Modifier.size(48.dp))
    }
}

// --- SEARCH BAR COMPONENT ---
@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(12.dp)),
            placeholder = {
                Text("Search for Landmarks", style = SearchTextStyle)
            },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = TextGray)
            },
            trailingIcon = if (query.isNotEmpty()) {
                {
                    IconButton(onClick = onClearClick) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextGray)
                    }
                }
            } else null,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = EgyptGold,
                unfocusedBorderColor = EgyptGold,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                cursorColor = EgyptGold,
                focusedTextColor = TextBlack,
                unfocusedTextColor = TextBlack
            ),
            singleLine = true
        )
    }
}

// --- LANDMARK GRID ---
@Composable
private fun LandmarkGrid(
    landmarks: List<LandMark>,
    onLandmarkClick: (Int) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(landmarks) { landmark ->
            LandmarkItem(
                landmark = landmark,
                onClick = { onLandmarkClick(landmark.id) }
            )
        }
    }
}

// --- LANDMARK ITEM CARD ---
@Composable
private fun LandmarkItem(
    landmark: LandMark,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        // Image Card
        Card(
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(128.dp)
        ) {
            if (landmark.imageUrl.isNullOrEmpty()) {
                // CASE 1: No Image URL -> Show Gray Placeholder
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Place,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }
            } else {
                // CASE 2: Valid Image URL -> Load with Coil
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(landmark.imageUrl)
                        .addHeader("User-Agent", "EgyptTourApp/1.0")
                        .crossfade(true)
                        .listener(
                            onError = { _, result ->
                                Log.e("CoilError", "Failed to load ${landmark.name}: ${result.throwable.message}")
                            }
                        )
                        .build(),
                    contentDescription = landmark.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    placeholder = rememberVectorPainter(Icons.Default.Place),
                    error = rememberVectorPainter(Icons.Default.Warning)
                )
            }
        }

        // Floating Label
        Surface(
            color = EgyptLightGold,
            shape = RoundedCornerShape(4.dp),
            shadowElevation = 2.dp,
            modifier = Modifier
                .widthIn(min = 100.dp, max = 160.dp)
                .offset(y = (-12).dp)
        ) {
            Text(
                text = landmark.name,
                style = LandmarkLabelTextStyle,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

// --- EMPTY STATE ---
@Composable
private fun EmptyState(message: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Place,
            contentDescription = null,
            tint = TextGray,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = TextGray,
            textAlign = TextAlign.Center
        )
    }
}

// --- ERROR STATE ---
@Composable
private fun ErrorState(
    message: String,
    onRetryClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = Color.Red,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "⚠️ Error",
            style = MaterialTheme.typography.headlineMedium,
            color = TextBlack
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = TextGray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRetryClick,
            colors = ButtonDefaults.buttonColors(containerColor = EgyptGold)
        ) {
            Text("Retry", color = TextWhite)
        }
    }
}

// --- ERROR SCREEN (For invalid governorate) ---
@Composable
private fun ErrorScreen(
    message: String,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = Color.Red,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = TextGray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onBackClick,
            colors = ButtonDefaults.buttonColors(containerColor = EgyptGold)
        ) {
            Text("Go Back", color = TextWhite)
        }
    }
}