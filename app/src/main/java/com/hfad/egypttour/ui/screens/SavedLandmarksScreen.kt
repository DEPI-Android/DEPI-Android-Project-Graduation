package com.hfad.egypttour.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.hfad.egypttour.data.model.LandMark
import com.hfad.egypttour.ui.theme.EgyptGold
import com.hfad.egypttour.ui.viewmodel.SavedLandmarksViewModel

@Composable
fun SavedLandmarksScreen(
    type: String, // "favorites" or "saves"
    onBackClick: () -> Unit,
    onLandmarkClick: (Int) -> Unit,
    viewModel: SavedLandmarksViewModel = hiltViewModel()
) {
    LaunchedEffect(type) {
        if (type == "favorites") viewModel.loadFavorites() else viewModel.loadSaves()
    }

    val landmarks by viewModel.savedLandmarks.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val title = if (type == "favorites") "My Favourites" else "Saved Places"

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(EgyptGold)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack, "Back",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.clickable { onBackClick() }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, fontSize = 20.sp, color = MaterialTheme.colorScheme.onPrimary)
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = EgyptGold)
            }
        } else if (landmarks.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No items found.")
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(landmarks) { landmark ->
                    SavedItemCard(landmark, onLandmarkClick)
                }
            }
        }
    }
}

@Composable
fun SavedItemCard(landmark: LandMark, onClick: (Int) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(100.dp).clickable { onClick(landmark.id) },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row {
            AsyncImage(
                //what url ?
                model = landmark.imageUrl, // ??????
                contentDescription = null,
                modifier = Modifier.width(100.dp).fillMaxHeight(),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.Center) {
                Text(landmark.name, fontSize = 16.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                Text(landmark.governorate.displayName, fontSize = 14.sp, color = androidx.compose.ui.graphics.Color.Gray)
            }
        }
    }
}