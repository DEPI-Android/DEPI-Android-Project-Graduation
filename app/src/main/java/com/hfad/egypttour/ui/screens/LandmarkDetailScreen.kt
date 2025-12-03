package com.hfad.egypttour.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.hfad.egypttour.data.model.LandMark
import com.hfad.egypttour.data.util.Result
import com.hfad.egypttour.ui.theme.EgyptGold
import com.hfad.egypttour.ui.theme.PureWhite
import com.hfad.egypttour.ui.theme.TextBlack
import com.hfad.egypttour.ui.theme.TextGray
import com.hfad.egypttour.ui.viewmodel.LandmarkListViewModel
import kotlinx.coroutines.delay

@Composable
fun LandmarkDetailScreen(
    landmarkId: Int,
    onBackClick: () -> Unit,
    viewModel: LandmarkListViewModel = hiltViewModel()
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

    // 1. Combine Main Image + Gallery into one list
    val allImages = remember(landmark) {
        val list = mutableListOf<String>()
        landmark.imageUrl?.let { list.add(it) }
        list.addAll(landmark.imageUrls)
        list
    }

    // 2. State to track which image is currently showing
    var currentImageIndex by remember { mutableIntStateOf(0) }

    // 3. State management for unified expandable button
    var isExpanded by remember { mutableStateOf(false) }
    var hasTimedOut by remember { mutableStateOf(false) }
    var isGlowing by remember { mutableStateOf(false) }
    
    // Combined expansion state: button expands when manually tapped OR timeout occurs
    val showExpandedState = isExpanded || hasTimedOut
    
    // 4. State for expandable details section
    var isDetailsExpanded by remember { mutableStateOf(false) }
    
    // Animated height for details section
    val detailsHeightFraction by animateFloatAsState(
        targetValue = if (isDetailsExpanded) 1f else 0.60f,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 250f),
        label = "detailsHeight"
    )
    
    // Description state for timeout detection
    val description = landmark.description
    val isPlaceholderDescription = description.equals("No description available.", ignoreCase = true) || 
                                    description.equals("building in Egypt", ignoreCase = true)
    
    // Timeout detection: Auto-expand button after 13 seconds if Wikipedia is loading
    // FIXED: Moved to outer scope so it actually triggers
    LaunchedEffect(landmark.id) {
        if ((description.isNullOrBlank() || isPlaceholderDescription) && landmark.needsWikipediaDescription) {
            delay(13000L) // 13 seconds
            hasTimedOut = true
        }
    }
    
    // Animation: Chevron rotation (0° = down, 180° = up)
    val chevronRotation by animateFloatAsState(
        targetValue = if (showExpandedState) 180f else 0f,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 400f),
        label = "chevronRotation"
    )
    
    // Animation: Glow effect for attention-grabbing
    val glowAlpha by animateFloatAsState(
        targetValue = if (isGlowing) 0.3f else 0f,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "glowAlpha"
    )
    
    // Trigger glow effect when button expands
    LaunchedEffect(showExpandedState) {
        if (showExpandedState) {
            isGlowing = true
            delay(2000L) // Glow for 2 seconds
            isGlowing = false
        }
    }

    // Helper function to go to next/prev image safely
    fun cycleImage(direction: Int) {
        if (allImages.isNotEmpty()) {
            val nextIndex = currentImageIndex + direction
            currentImageIndex = when {
                nextIndex < 0 -> allImages.size - 1 // Wrap to end
                nextIndex >= allImages.size -> 0   // Wrap to start
                else -> nextIndex
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // 1. TOP IMAGE AREA
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.45f) // Takes top 45%
                .align(Alignment.TopCenter)
        ) {
            if (allImages.isNotEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(allImages[currentImageIndex])
                        .addHeader("User-Agent", "EgyptTourApp/1.0")
                        .crossfade(true)
                        .build(),
                    contentDescription = landmark.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(modifier = Modifier.fillMaxSize().background(Color.LightGray))
            }
            // Gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Brush.verticalGradient(listOf(Color.Black.copy(0.5f), Color.Transparent)))
                    .align(Alignment.TopCenter)
            )
            // --- CAROUSEL ARROWS (Only if more than 1 image) ---
            if (allImages.size > 1) {
                // Left Arrow
                IconButton(
                    onClick = { cycleImage(-1) },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 8.dp)
                        .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Previous Image",
                        tint = Color.White
                    )
                }

                // Right Arrow
                IconButton(
                    onClick = { cycleImage(1) },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 8.dp)
                        .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next Image",
                        tint = Color.White
                    )
                }
            }
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

        // 3. EXPANDABLE WHITE DETAILS SHEET
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(detailsHeightFraction) // Animated height
                .align(Alignment.BottomCenter)
                .clickable(enabled = false) { } // Prevent click-through
                // Add bidirectional swipe gesture detection
                .pointerInput(Unit) {
                    var totalDrag = 0f
                    detectVerticalDragGestures(
                        onDragStart = { totalDrag = 0f },
                        onDragEnd = {
                            // Check total accumulated drag to determine action
                            when {
                                // Swipe down to collapse (when expanded)
                                isDetailsExpanded && totalDrag > 150f -> {
                                    isDetailsExpanded = false
                                }
                                // Swipe up to expand (when collapsed)
                                !isDetailsExpanded && totalDrag < -150f -> {
                                    isDetailsExpanded = true
                                }
                            }
                            totalDrag = 0f
                        },
                        onVerticalDrag = { change, dragAmount ->
                            totalDrag += dragAmount
                            change.consume()
                        }
                    )
                },
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = PureWhite,
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // Drag handle indicator - Visual indicator for swipe gestures
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Drag handle bar
                        Box(
                            modifier = Modifier
                                .width(50.dp)
                                .height(5.dp)
                                .background(
                                    if (isDetailsExpanded) EgyptGold.copy(alpha = 0.6f) 
                                    else TextGray.copy(alpha = 0.4f), 
                                    RoundedCornerShape(3.dp)
                                )
                        )
                        
                        // Hint text based on state
                        Text(
                            text = if (isDetailsExpanded) "Swipe down to collapse" else "Swipe up to expand",
                            fontSize = 11.sp,
                            color = TextGray.copy(alpha = 0.6f),
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                }
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
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

                    Text("Details", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = EgyptGold, modifier = Modifier.align(Alignment.CenterHorizontally))
                    Spacer(modifier = Modifier.height(8.dp))
                    
                     // Description display logic
                    // Note: description state and timeout detection moved to outer scope
                    
                    when {
                        // Show Wikipedia loading indicator ONLY if still loading AND not timed out
                        (description.isNullOrBlank() || isPlaceholderDescription) && landmark.needsWikipediaDescription && !hasTimedOut -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(
                                    color = EgyptGold,
                                    modifier = Modifier.size(32.dp),
                                    strokeWidth = 3.dp
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Getting details from Wikipedia...",
                                    fontSize = 14.sp,
                                    color = TextGray,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                )
                            }
                        }
                        // Show description if available and not a placeholder
                        !description.isNullOrBlank() && !isPlaceholderDescription -> {
                            Text(
                                text = description,
                                fontSize = 15.sp,
                                color = TextBlack.copy(0.7f),
                                textAlign = TextAlign.Justify,
                                lineHeight = 24.sp
                            )
                        }
                        // Timeout occurred: Show centered message (buttons will be shown below)
                        hasTimedOut -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = EgyptGold,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Wikipedia is taking longer than expected",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextBlack,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Try searching on Google for more information about ${landmark.name}",
                                    fontSize = 15.sp,
                                    color = TextGray,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 22.sp,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                        }
                        // Fallback: Show message if no description available
                        else -> {
                            Text(
                                text = "Information about this landmark is limited in our database.",
                                fontSize = 15.sp,
                                color = TextBlack.copy(0.6f),
                                textAlign = TextAlign.Center,
                                lineHeight = 24.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Unified Expandable Button Section
                // Bottom padding increases when expanded for better spacing
                val bottomPadding by animateDpAsState(
                    targetValue = if (showExpandedState) 24.dp else 12.dp,
                    animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f),
                    label = "bottomPadding"
                )
                
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = bottomPadding),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Primary button: Find Location on Map (always visible)
                    // FIXED: Tapping toggles expansion, or executes map action when expanded
                    Button(
                        onClick = {
                            if (!showExpandedState) {
                                // Collapsed: expand to show both options
                                isExpanded = true
                            } else {
                                // Expanded: toggle collapse OR execute map action
                                // If user taps again while expanded, collapse it
                                if (isExpanded) {
                                    isExpanded = false
                                } else {
                                    // Auto-expanded (timeout): execute map action
                                    val lat = landmark.lat ?: 0.0
                                    val lon = landmark.lon ?: 0.0
                                    val uri = Uri.parse("geo:$lat,$lon?q=$lat,$lon(${landmark.name})")
                                    val intent = Intent(Intent.ACTION_VIEW, uri)
                                    context.startActivity(intent)
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .shadow(
                                elevation = if (isGlowing && !showExpandedState) 12.dp else 8.dp,
                                shape = RoundedCornerShape(16.dp),
                                ambientColor = EgyptGold.copy(alpha = if (!showExpandedState) glowAlpha else 0f),
                                spotColor = EgyptGold.copy(alpha = if (!showExpandedState) glowAlpha else 0f)
                            ),
                        colors = ButtonDefaults.buttonColors(containerColor = EgyptGold),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Find Location on Map",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            
                            // Chevron indicator (rotates when expanded)
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = if (showExpandedState) "Collapse" else "Expand",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(24.dp)
                                    .graphicsLayer {
                                        rotationZ = chevronRotation
                                    }
                            )
                        }
                    }
                    
                    // Google Search button (slides in when expanded)
                    AnimatedVisibility(
                        visible = showExpandedState,
                        enter = slideInVertically(
                            initialOffsetY = { it },
                            animationSpec = spring(dampingRatio = 0.7f, stiffness = 250f)
                        ) + fadeIn(tween(300)),
                        exit = slideOutVertically(
                            targetOffsetY = { it },
                            animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f)
                        ) + fadeOut(tween(200))
                    ) {
                        OutlinedButton(
                            onClick = {
                                val searchQuery = "${landmark.name} Egypt"
                                val googleSearchUrl = "https://www.google.com/search?q=${java.net.URLEncoder.encode(searchQuery, "UTF-8")}"
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(googleSearchUrl))
                                context.startActivity(intent)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .shadow(
                                    elevation = if (isGlowing) 12.dp else 6.dp,
                                    shape = RoundedCornerShape(16.dp),
                                    ambientColor = EgyptGold.copy(alpha = glowAlpha),
                                    spotColor = EgyptGold.copy(alpha = glowAlpha)
                                ),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = EgyptGold
                            ),
                            border = BorderStroke(2.dp, EgyptGold),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = EgyptGold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Search google for ${landmark.name}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = EgyptGold
                            )
                        }
                    }
                }
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