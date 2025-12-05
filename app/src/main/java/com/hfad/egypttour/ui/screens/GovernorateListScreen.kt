package com.hfad.egypttour.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.Image // Import the Composable Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
//import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource // Needed for R.drawable loading
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.hfad.egypttour.data.model.Governorate
import com.hfad.egypttour.ui.theme.EgyptGold
import com.hfad.egypttour.ui.theme.TextBlack
import com.hfad.egypttour.ui.theme.TextGray
import com.hfad.egypttour.ui.theme.PureWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GovernorateListScreen(
    onGovernorateClick: (String) -> Unit,
    onProfileClick: () -> Unit
) {
    // Using Kotlin 1.9+ 'entries'. If you are on older Kotlin, change to .values().toList()
    val governorates = Governorate.entries.toList()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "🇪🇬 Egypt Tour",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextBlack
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onProfileClick
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = TextBlack,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = EgyptGold
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 12.dp)
        ) {
            Text(
                text = "Discover Egypt's Governorates",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextBlack
            )

            Text(
                text = "Tap a card to explore landmarks",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                fontSize = 13.sp,
                color = TextGray
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(governorates) { governorate ->
                    GovernorateCard(
                        governorate = governorate,
                        onClick = { onGovernorateClick(governorate.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun GovernorateCard(
    governorate: Governorate,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp,
            pressedElevation = 12.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = PureWhite
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // IMAGES: This handles the network loading securely
//            SubcomposeAsyncImage(
//                model = governorate.imageUrl,
//                contentDescription = governorate.displayName,
//                modifier = Modifier.fillMaxSize(),
//                contentScale = ContentScale.Crop,
//                alpha = 0.9f,
//                loading = {
//                    Box(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .background(TextGray.copy(alpha = 0.3f)),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        CircularProgressIndicator(
//                            modifier = Modifier.size(40.dp),
//                            color = EgyptGold
//                        )
//                    }
//                },
//                error = {
//                    Box(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .background(TextGray.copy(alpha = 0.5f)),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Icon(
//                            imageVector = Icons.Default.Image,
//                            contentDescription = "Image load error",
//                            tint = TextBlack.copy(alpha = 0.5f),
//                            modifier = Modifier.size(40.dp)
//                        )
//                    }
//                }
//            )

            // In GovernorateListScreen.ktgh

            Image(
                painter = painterResource(id = governorate.imageRes),
                contentDescription = governorate.displayName,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Gradient Overlay for readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.BottomCenter)
                    .background(TextBlack.copy(alpha = 0.3f))
            )

            // Card Text Content
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = governorate.displayName,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = PureWhite,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = EgyptGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Tap to explore",
                        fontSize = 11.sp,
                        color = EgyptGold,
                        modifier = Modifier.padding(start = 4.dp),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Top Gold Bar Accent
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(EgyptGold)
                    .align(Alignment.TopCenter)
            )
        }
    }
}