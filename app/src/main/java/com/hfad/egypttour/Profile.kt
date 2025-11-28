package com.hfad.egypttour.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hfad.egypttour.R

@Composable
fun Profile() {
    // Main container - fills entire screen with SoftWhite background
    Box(
        modifier = Modifier
            .background(Color(0xFFFAFAFA))
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar (Back button and Edit button)
            TopBar()

            // Profile Header (Image, Name, Title, Contact Info)
            ProfileHeader()

            Spacer(modifier = Modifier.height(32.dp))

            // Menu Items Section
            MenuSection()
        }
    }
}

// ==================== TOP BAR ====================
@Composable
fun TopBar() {
    // Row arranges items horizontally (left to right)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,  // Space items to edges
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back button (left side)
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back button",
            tint = Color(0xFF333333),
            modifier = Modifier
                .size(28.dp)
                .clickable { /* Handle back navigation */ }
        )

        // Edit button (right side)
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Edit profile",
            tint = Color(0xFF333333),
            modifier = Modifier
                .size(24.dp)
                .clickable { /* Handle edit profile */ }
        )
    }
}

// ==================== PROFILE HEADER ====================
@Composable
fun ProfileHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Image in circular shape
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        ) {
            // Replace with actual image
            // For now, shows a placeholder
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Profile picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Name
        Text(
            text = "Youssef Wahba",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF333333) // Text black COLOR
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Title/Role
        Text(
            text = "Android Developer",
            fontSize = 14.sp,
            color = Color(0xFF666666) // Text gray COLOR
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Contact Information
        ContactInfoRow()
    }
}

// ==================== CONTACT INFO ====================
@Composable
fun ContactInfoRow() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        // Phone Number
        ContactInfoItem(
            icon = Icons.Default.Phone,
            text = "01092723109"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Email
        ContactInfoItem(
            icon = Icons.Default.Email,
            text = "youssefwahba47@gmail.com"
        )
    }
}

@Composable
fun ContactInfoItem(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF666666),  // Text gray COLOR
            modifier = Modifier.size(18.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = text,
            fontSize = 14.sp,
            color = Color(0xFF333333),  // Text black COLOR
        )
    }
}

// ==================== MENU SECTION ====================
@Composable
fun MenuSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        // My Favourites
        MenuItem(
            icon = Icons.Default.Favorite,
            iconTint = Color(0xFFE4B643),  // Gold COLOR
            text = "My Favourites",
            onClick = { /* Navigate to favourites */ }
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Saves
        MenuItem(
            icon = Icons.Default.CheckCircle,
            iconTint = Color(0xFFE4B643),  // Gold COLOR
            text = "Saves",
            onClick = { /* Navigate to saves */ }
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Settings
        MenuItem(
            icon = Icons.Default.Settings,
            iconTint = Color(0xFFE4B643),  // Gold COLOR,
            text = "Settings",
            onClick = { /* Navigate to settings */ }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Log Out (Special styling - red text)
        MenuItem(
            icon = Icons.Default.ExitToApp,
            iconTint = Color(0xFFB00020), // Error red COLOR
            text = "Log out",
            textColor = Color(0xFFB00020),  // Error red COLOR
            onClick = { /* Handle logout */ }
        )
    }
}

// ==================== MENU ITEM (REUSABLE) ====================
@Composable
fun MenuItem(
    icon: ImageVector,
    iconTint: Color,
    text: String,
    textColor: Color = Color(0xFF333333),  // Text black COLOR
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFFFFFF))  // pure white COLOR
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Text
        Text(
            text = text,
            fontSize = 16.sp,
            color = textColor,
            modifier = Modifier.weight(1f)  // Takes up remaining space
        )

        // Arrow icon (right side)
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "Navigate",
            tint = Color(0xFF333333),   // text gray COLOR
            modifier = Modifier.size(24.dp)
        )
    }
}

// ==================== PREVIEW ====================
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfilePreview() {
    Profile()
}