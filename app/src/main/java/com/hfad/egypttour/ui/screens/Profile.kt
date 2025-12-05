package com.hfad.egypttour.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hfad.egypttour.Login.LoginActivity
import com.hfad.egypttour.data.model.User
import com.hfad.egypttour.ui.viewmodel.ProfileUiState
import com.hfad.egypttour.ui.viewmodel.ProfileViewModel
import androidx.compose.material.icons.filled.BookmarkBorder

@Composable
fun Profile(
    onBackClick: () -> Unit,
    onLogoutSuccess: () -> Unit = {},
    onNavigateToSaved: (String) -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val logoutEvent by viewModel.logoutEvent.collectAsState()
    val isLoggingOutFromAllDevices by viewModel.isLoggingOutFromAllDevices.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Fetch user data when screen is first displayed
    LaunchedEffect(Unit) {
        viewModel.fetchUserData()
    }

    // Handle logout event - navigate to LoginActivity
    LaunchedEffect(logoutEvent) {
        if (logoutEvent) {
            val intent = Intent(context, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
            viewModel.onLogoutEventHandled()
        }
    }

    // Enhanced logout dialog with two options
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { 
                Text(
                    "Log Out",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ) 
            },
            text = { 
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Choose logout option:",
                        fontSize = 14.sp,
                        color = Color(0xFF666666)
                    )
                }
            },
            confirmButton = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Option 1: Logout from this device only
                    Button(
                        onClick = {
                            showLogoutDialog = false
                            viewModel.signOut()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE4B643)
                        ),
                        enabled = !isLoggingOutFromAllDevices
                    ) {
                        Text("Logout (This Device Only)")
                    }

                    // Option 2: Logout from all devices
                    Button(
                        onClick = {
                            showLogoutDialog = false
                            viewModel.signOutFromAllDevices()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFB00020)
                        ),
                        enabled = !isLoggingOutFromAllDevices
                    ) {
                        if (isLoggingOutFromAllDevices) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.White
                            )
                        } else {
                            Text("Logout from All Devices")
                        }
                    }

                    // Cancel button
                    TextButton(
                        onClick = { showLogoutDialog = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cancel")
                    }
                }
            },
            dismissButton = null
        )
    }

    // Main container
    Box(
        modifier = Modifier
            .background(Color(0xFFFAFAFA))
            .fillMaxSize()
            .padding(top = 16.dp)
    ) {
        // Show different content based on state
        when (val state = uiState) {
            is ProfileUiState.Loading -> {
                LoadingContent()
            }
            is ProfileUiState.Success -> {
                ProfileContent(
                    user = state.user,
                    onBackClick = onBackClick,
                    onLogout = { showLogoutDialog = true },
                    onNavigateToSaved = onNavigateToSaved
                )
            }
            is ProfileUiState.Error -> {
                ErrorContent(
                    message = state.message,
                    onRetry = { viewModel.fetchUserData() },
                    onBackClick = onBackClick
                )
            }
            is ProfileUiState.NotAuthenticated -> {
                NotAuthenticatedContent(onBackClick = onBackClick)
            }
        }
    }
}

// ==================== LOADING STATE ====================
@Composable
fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = Color(0xFFE4B643)
            )
            Text(
                text = "Loading profile...",
                fontSize = 16.sp,
                color = Color(0xFF666666)
            )
        }
    }
}

// ==================== ERROR STATE ====================
@Composable
fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top bar with back button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back button",
                tint = Color(0xFF333333),
                modifier = Modifier
                    .size(28.dp)
                    .clickable { onBackClick() }
            )
        }

        // Error content
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Error",
                    tint = Color(0xFFB00020),
                    modifier = Modifier.size(64.dp)
                )

                Text(
                    text = "Oops! Something went wrong",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )

                Text(
                    text = message,
                    fontSize = 14.sp,
                    color = Color(0xFF666666),
                    textAlign = TextAlign.Center
                )

                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE4B643)
                    ),
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Retry", color = Color.White)
                }
            }
        }
    }
}

// ==================== NOT AUTHENTICATED STATE ====================
@Composable
fun NotAuthenticatedContent(onBackClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top bar with back button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back button",
                tint = Color(0xFF333333),
                modifier = Modifier
                    .size(28.dp)
                    .clickable { onBackClick() }
            )
        }

        // Not authenticated content
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Not logged in",
                    tint = Color(0xFF666666),
                    modifier = Modifier.size(64.dp)
                )

                Text(
                    text = "Not Logged In",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )

                Text(
                    text = "Please sign in to view your profile",
                    fontSize = 14.sp,
                    color = Color(0xFF666666),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// ==================== SUCCESS STATE - PROFILE CONTENT ====================
@Composable
fun ProfileContent(
    user: User,
    onBackClick: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToSaved: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top Bar
        TopBar(onBackClick = onBackClick)

        // Profile Header with user data
        ProfileHeader(
            username = user.username,
            email = user.email
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Menu Section
        MenuSection(onLogout = onLogout, onNavigateToSaved = onNavigateToSaved)
    }
}

// ==================== TOP BAR ====================
@Composable
fun TopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back button
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back button",
            tint = Color(0xFF333333),
            modifier = Modifier
                .size(28.dp)
                .clickable { onBackClick() }
        )
    }
}

// ==================== PROFILE HEADER ====================
@Composable
fun ProfileHeader(
    username: String,
    email: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Image
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color(0xFFE4B643))
        ) {
            // Display first letter of username
            Text(
                text = username.firstOrNull()?.uppercase() ?: "U",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display name (show username if available, otherwise 'Welcome')
        Text(
            text = if (username.isNotEmpty()) username else "Welcome!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF333333)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Email and Username (from Firebase)
        ContactInfoRow(email = email, username = username)
    }
}

// ==================== CONTACT INFO ====================
@Composable
fun ContactInfoRow(email: String, username: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Email row
        ContactInfoItem(
            icon = Icons.Default.Email,
            label = "Email",
            text = email
        )
        
        // Username row
        ContactInfoItem(
            icon = Icons.Default.Person,
            label = "Username",
            text = if (username.isNotEmpty()) username else "Not set"
        )
    }
}

@Composable
fun ContactInfoItem(icon: ImageVector, label: String, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        // Icon
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFFE4B643),
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Label and Value
        Column {
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color(0xFF999999)
            )
            Text(
                text = text,
                fontSize = 14.sp,
                color = Color(0xFF333333),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// ==================== MENU SECTION ====================
@Composable
fun MenuSection(onLogout: () -> Unit, onNavigateToSaved: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        // My Favourites
        MenuItem(
            icon = Icons.Default.Favorite,
            iconTint = Color(0xFFE4B643),
            text = "My Favourites",
            onClick = { onNavigateToSaved("favorites") }
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Saves
        MenuItem(
            icon = Icons.Default.BookmarkBorder,
            iconTint = Color(0xFFE4B643),
            text = "Saves",
            onClick = { onNavigateToSaved("saves") }
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Settings
        MenuItem(
            icon = Icons.Default.Settings,
            iconTint = Color(0xFFE4B643),
            text = "Settings",
            onClick = { /* Navigate to settings */ }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Log Out
        MenuItem(
            icon = Icons.Default.ExitToApp,
            iconTint = Color(0xFFB00020),
            text = "Log out",
            textColor = Color(0xFFB00020),
            onClick = onLogout
        )
    }
}

// ==================== MENU ITEM ====================
@Composable
fun MenuItem(
    icon: ImageVector,
    iconTint: Color,
    text: String,
    textColor: Color = Color(0xFF333333),
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFFFFFF))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = text,
            fontSize = 16.sp,
            color = textColor,
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "Navigate",
            tint = Color(0xFF666666),
            modifier = Modifier.size(24.dp)
        )
    }
}

// ==================== PREVIEW ====================
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfilePreview() {
    Profile(
        onBackClick = {},
        onLogoutSuccess = {},
        onNavigateToSaved = {}
    )
}