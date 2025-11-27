package com.hfad.egypttour

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.hfad.egypttour.data.model.LandMark
import com.hfad.egypttour.ui.theme.*

// --- MAIN SCREEN ---
@Composable
fun LandmarksScreen(
    cityName: String,
    landmarks: List<LandMark>,
    isLoading: Boolean,
    onBackClick: () -> Unit
) {
    var query by remember { mutableStateOf("") }

    val filteredLandmarks = landmarks.filter {
        it.name.contains(query, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // --- HEADER ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(EgyptGold)
                .padding(top = 48.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextWhite, modifier = Modifier.size(28.dp))
            }
            Text(
                text = cityName,
                style = CityTitleTextStyle,
                color = TextWhite
            )
            IconButton(onClick = { /* Handle Home */ }) {
                Icon(Icons.Default.Home, contentDescription = "Home", tint = TextWhite, modifier = Modifier.size(28.dp))
            }
        }

        // --- SEARCH BAR ---
        Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(12.dp)),
                placeholder = { Text("Search for Landmarks", style = SearchTextStyle) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextGray) },
                trailingIcon = if (query.isNotEmpty()) {
                    { IconButton(onClick = { query = "" }) { Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextGray) } }
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

        // --- CONTENT AREA ---
        Box(
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = EgyptGold)
            } else {
                if (filteredLandmarks.isEmpty()) {
                    Text("No landmarks found", color = TextGray)
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredLandmarks) { landmark ->
                            LandmarkItem(landmark)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LandmarkItem(landmark: LandMark) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Image Card
        Card(
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth().height(128.dp)
        ) {
            if (landmark.imageUrl.isNullOrEmpty()) {
                // CASE 1: No Image URL -> Show Gray Placeholder
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Place, contentDescription = null, tint = Color.White, modifier = Modifier.size(48.dp))
                }
            } else {
                // CASE 2: Valid Image URL -> Load with Coil
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(landmark.imageUrl)
                        // FIX: Add User-Agent header so Wikipedia doesn't block the image request
                        .addHeader("User-Agent", "EgyptTourApp/1.0")
                        .crossfade(true)
                        .listener(
                            onError = { _, result ->
                                // This prints the EXACT error to your Logcat so we know why it fails
                                Log.e("CoilError", "Failed to load ${landmark.name}: ${result.throwable.message}")
                            }
                        )
                        .build(),
                    contentDescription = landmark.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    // While loading, show the pin icon
                    placeholder = rememberVectorPainter(Icons.Default.Place),
                    // If loading fails, show a warning icon (Red)
                    error = rememberVectorPainter(Icons.Default.Warning)
                )
            }
        }

        // Floating Label
        Surface(
            color = EgyptLightGold,
            shape = RoundedCornerShape(4.dp),
            shadowElevation = 2.dp,
            modifier = Modifier.widthIn(min = 100.dp, max = 160.dp).offset(y = (-12).dp)
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