package com.hfad.egypttour.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.hfad.egypttour.data.model.LandMark
import com.hfad.egypttour.ui.theme.*
import com.hfad.egypttour.ui.viewmodel.LandmarkListViewModel

/**
 * Landmark Detail Screen
 */
@Composable
fun LandmarkDetailScreen(
    landmarkId: Int,
    onBackClick: () -> Unit,
    viewModel: LandmarkListViewModel = viewModel()
) {


    // Only call content if landmark is not null
    // Load landmark
    LaunchedEffect(landmarkId) {
        viewModel.loadLandmarkDetails(landmarkId)
    }

    val state by viewModel.selectedLandmark.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when (state) {
            is Result.Loading -> {
                CircularProgressIndicator(
                    color = EgyptGold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is Result.Error -> {
                ErrorState(
                    message = "Failed to load landmark details",
                    onRetryClick = { viewModel.loadLandmarkDetails(landmarkId) }
                )
            }

            is Result.Success -> {
                val landmark = (state as Result.Success<LandMark>).data
                LandmarkDetailContent(landmark, onBackClick)
            }

            else -> {}
        }
    }
}

/* ---------------------- SCREEN CONTENT ---------------------- */

@Composable
private fun LandmarkDetailContent(
    landmark: LandMark,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        // ------- HEADER + IMAGE -------
        Box {
            LandmarkMainImage(url = landmark.imageUrl)

            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .padding(16.dp)
                    .size(40.dp)
                    .align(Alignment.TopStart)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextWhite
                )
            }
        }

        // ------- TITLE -------
        Text(
            text = landmark.name,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = TextBlack
            ),
            modifier = Modifier.padding(16.dp)
        )

        // ------- GOVERNORATE TAG -------
        Surface(
            color = EgyptLightGold,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Governorate: ${landmark.governorate.displayName}",
                color = TextBlack,
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ------- GALLERY -------
        if (landmark.allImages.size > 1) {
            GallerySection(landmark.allImages)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // ------- DESCRIPTION -------
        Text(
            text = landmark.description,
            style = MaterialTheme.typography.bodyLarge.copy(color = TextBlack),
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ------- LOCATION -------
        if (landmark.haseCoordinates) {
            LocationSection(lat = landmark.lat!!, lon = landmark.lon!!)
        }

        Spacer(modifier = Modifier.height(50.dp))
    }
}

/* ---------------------- COMPONENTS ---------------------- */

// Main image at top
@Composable
private fun LandmarkMainImage(url: String?) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .crossfade(true)
            .build(),
        contentDescription = "Main Image",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp),
        placeholder = rememberVectorPainter(Icons.Default.Place),
        error = rememberVectorPainter(Icons.Default.Warning)
    )
}

// Horizontal gallery images
@Composable
private fun GallerySection(images: List<String>) {
    Column(Modifier.padding(16.dp)) {
        Text(
            text = "Gallery",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = TextBlack
        )
        Spacer(Modifier.height(12.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(images) { url ->
                Card(
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(140.dp)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(url)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        placeholder = rememberVectorPainter(Icons.Default.Place),
                        error = rememberVectorPainter(Icons.Default.Warning)
                    )
                }
            }
        }
    }
}

@Composable
private fun LocationSection(lat: Double, lon: Double) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Location",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = TextBlack
        )
        Spacer(Modifier.height(6.dp))

        Text(
            text = "Latitude:  $lat\nLongitude: $lon",
            style = MaterialTheme.typography.bodyMedium,
            color = TextGray
        )
    }
}
