package com.hfad.egypttour.ui.screens



import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.Default

import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.hfad.egypttour.data.model.Governorate
import com.hfad.egypttour.data.model.LandMark
 import com.hfad.egypttour.data.model.Result
import com.hfad.egypttour.ui.viewmodel.LandmarkListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandmarkListScreen(
    governorateId: String,
    onLandmarkClick: (Int) -> Unit,
    viewModel: LandmarkListViewModel = viewModel()
) {
    val governorate = remember(governorateId) {
        Governorate.fromId(governorateId)
    }

    if (governorate == null) {
        ErrorScreen("Invalid governorate")
        return
    }

    // Load landmarks when screen opens
    LaunchedEffect(governorate) {
        viewModel.loadLandmarks(governorate)
        viewModel.resetScrollPosition()  // NEW: Reset scroll when switching governorates
    }

    val state by viewModel.landmarksState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filteredLandmarks by viewModel.filteredLandmarks.collectAsState()
    val scrollPosition by viewModel.scrollPosition.collectAsState()  // NEW

    // NEW: Scroll state restoration
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = scrollPosition)

    // NEW: Save scroll position when it changes
    LaunchedEffect(listState.firstVisibleItemIndex) {
        viewModel.saveScrollPosition(listState.firstVisibleItemIndex)
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text(governorate.displayName) }
                )

                if (state is Result.Success) {
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = { viewModel.updateSearchQuery(it) },
                        onClearClick = { viewModel.clearSearch() }
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (state) {
                is Result.Loading -> {
                    LoadingScreen()
                }

                is Result.Success -> {
                    if (filteredLandmarks.isEmpty()) {
                        EmptyScreen(
                            message = if (searchQuery.isBlank()) {
                                "No landmarks found"
                            } else {
                                "No results for '$searchQuery'"
                            }
                        )
                    } else {
                        LandmarkList(
                            landmarks = filteredLandmarks,
                            listState = listState,  // NEW: Pass scroll state
                            onLandmarkClick = onLandmarkClick
                        )
                    }
                }

                is Result.Error -> {
                    ErrorScreen(
                        message = viewModel.errorMessage ?: "Unknown error",
                        onRetryClick = { viewModel.retry() }
                    )
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearClick: () -> Unit
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = { Text("Search landmarks...") },
        leadingIcon = { Icon(Icons.Default.Search, "Search") },
        trailingIcon = {
            if (query.isNotBlank()) {
                IconButton(onClick = onClearClick) {
                    Icon(Icons.Default.Clear, "Clear")
                }
            }
        },
        singleLine = true
    )
}

/**
 * UPDATED: Now accepts LazyListState for scroll restoration
 */
@Composable
fun LandmarkList(
    landmarks: List<LandMark>,
    listState: LazyListState,  // NEW: Scroll state parameter
    onLandmarkClick: (Int) -> Unit
) {
    LazyColumn(
        state = listState,  // NEW: Use provided scroll state
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(landmarks) { landmark ->
            LandmarkCard(
                landmark = landmark,
                onClick = { onLandmarkClick(landmark.id) }
            )
        }
    }
}

/**
 * UPDATED: Now shows gallery indicator for multi-photo landmarks
 */
@Composable
fun LandmarkCard(
    landmark: LandMark,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(modifier = Modifier.height(120.dp)) {
            Box(modifier = Modifier.width(120.dp)) {
                AsyncImage(
                    model = landmark.imageUrl,
                    contentDescription = landmark.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // NEW: Gallery indicator badge
                if (landmark.hasGallery) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp),
                        shape = RoundedCornerShape(4.dp),
                        color = Color.Black.copy(alpha = 0.7f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "${landmark.totalImages}",
                                color = Color.White,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                Text(
                    text = landmark.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2  // NEW: Prevent overflow for long names
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = landmark.shortDescription,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 3
                )

                if (landmark.haseCoordinates) {
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "📍 View on map",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }
}

@Composable
fun ErrorScreen(message: String, onRetryClick: (() -> Unit)? = null) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "⚠️ Error",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium
        )
        if (onRetryClick != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetryClick) {
                Text("Retry")
            }
        }
    }
}
@Composable
fun EmptyScreen(message: String) {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = message,
            modifier = Modifier.align(Alignment.Center),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}