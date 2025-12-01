
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
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.hfad.egypttour.data.model.Governorate
import com.hfad.egypttour.data.model.LandMark
import com.hfad.egypttour.data.util.Result
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
    viewModel: LandmarkListViewModel = hiltViewModel()
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