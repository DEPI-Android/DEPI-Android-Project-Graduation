package com.hfad.egypttour.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.hfad.egypttour.data.model.LandMark
import com.hfad.egypttour.data.model.Result
import com.hfad.egypttour.ui.theme.EgyptGold
import com.hfad.egypttour.ui.theme.PureWhite
import com.hfad.egypttour.ui.theme.TextBlack
import com.hfad.egypttour.ui.theme.TextGray
import com.hfad.egypttour.ui.viewmodel.LandmarkListViewModel

@Composable
fun LandmarkDetailScreen(
    landmarkId: Int,
    onBackClick: () -> Unit,
    viewModel: LandmarkListViewModel = viewModel()
) {
    LaunchedEffect(landmarkId) {
        viewModel.loadLandmarkDetails(landmarkId)
    }

    // Observe State
    val state = viewModel.selectedLandmark.collectAsState().value

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        // SIMPLE STATE HANDLING
        if (state is Result.Loading) {
            CircularProgressIndicator(color = EgyptGold)
        }
        else if (state is Result.Error) {
            Text("Error loading details")
        }
        else if (state is Result.Success<*>) {
            // Safe cast to get data
            val landmark = (state as? Result.Success<LandMark?>)?.data

            if (landmark != null) {
                LandmarkDetailContent(landmark, onBackClick)
            } else {
                Text("Landmark not found")
            }
        }
    }
}

@Composable
private fun LandmarkDetailContent(
    landmark: LandMark,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    // Allow switching the main image
    var currentMainImage by remember { mutableStateOf(landmark.imageUrl) }

    Box(modifier = Modifier.fillMaxSize()) {

        // 1. TOP IMAGE AREA
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.45f) // Takes top 45%
                .align(Alignment.TopCenter)
        ) {
            if (!currentMainImage.isNullOrEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(currentMainImage)
                        .addHeader("User-Agent", "EgyptTourApp/1.0")
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(modifier = Modifier.fillMaxSize().background(Color.LightGray))
            }
            // Gradient
            Box(
                modifier = Modifier.fillMaxWidth().height(100.dp)
                    .background(Brush.verticalGradient(listOf(Color.Black.copy(0.5f), Color.Transparent)))
            )
        }

        // 2. ICONS (Back, Save, Fav)
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 48.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.background(Color.White.copy(0.8f), CircleShape)
            ) {
                Icon(Icons.Default.ArrowBack, "Back", tint = EgyptGold)
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                IconButton(onClick = {}, modifier = Modifier.background(Color.White.copy(0.8f), CircleShape)) {
                    Icon(Icons.Default.BookmarkBorder, "Save", tint = EgyptGold)
                }
                IconButton(onClick = {}, modifier = Modifier.background(Color.White.copy(0.8f), CircleShape)) {
                    Icon(Icons.Default.FavoriteBorder, "Fav", tint = EgyptGold)
                }
            }
        }

        // 3. WHITE DETAILS SHEET
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.60f) // Overlaps image
                .align(Alignment.BottomCenter),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = PureWhite,
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 32.dp, start = 24.dp, end = 24.dp, bottom = 16.dp)
            ) {
                // Title
                Text(
                    text = landmark.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = EgyptGold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    lineHeight = 32.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Location
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.LocationOn, null, tint = TextGray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${landmark.governorate.displayName}, Egypt", fontSize = 14.sp, color = TextGray)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Content
                Column(modifier = Modifier.weight(1f).verticalScroll(scrollState)) {

                    // GALLERY ROW (Visible if multiple images exist)
                    if (landmark.imageUrls.isNotEmpty()) {
                        Text("Photos", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = EgyptGold)
                        LazyRow(
                            contentPadding = PaddingValues(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item {
                                // Main Image
                                GalleryThumb(landmark.imageUrl, currentMainImage == landmark.imageUrl) {
                                    currentMainImage = landmark.imageUrl
                                }
                            }
                            items(landmark.imageUrls) { url ->
                                GalleryThumb(url, currentMainImage == url) { currentMainImage = url }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    Text("Details", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = EgyptGold, modifier = Modifier.align(Alignment.CenterHorizontally))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(landmark.description, fontSize = 15.sp, color = TextBlack.copy(0.7f), textAlign = TextAlign.Justify, lineHeight = 24.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val lat = landmark.lat ?: 0.0
                        val lon = landmark.lon ?: 0.0
                        val uri = Uri.parse("geo:$lat,$lon?q=$lat,$lon(${landmark.name})")
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp).shadow(4.dp, RoundedCornerShape(12.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = EgyptGold),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Find Location on Map", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun GalleryThumb(url: String?, isSelected: Boolean, onClick: () -> Unit) {
    if (url.isNullOrEmpty()) return

    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.size(80.dp).clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = if(isSelected) EgyptGold else Color.Transparent)
    ) {
        AsyncImage(
            model = url,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().padding(if(isSelected) 2.dp else 0.dp).clip(RoundedCornerShape(10.dp))
        )
    }
}