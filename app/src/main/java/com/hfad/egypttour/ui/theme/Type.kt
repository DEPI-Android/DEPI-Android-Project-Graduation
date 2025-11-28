package com.hfad.egypttour.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with


    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */


val CityFont = FontFamily.Serif
val SearchFont = FontFamily.Default
val LabelFont = FontFamily.Default

// -------------------------------
// 2. Custom Text Styles
// -------------------------------


// City name at top (e.g., "Cairo")
val CityTitleTextStyle = TextStyle(
    fontFamily = CityFont,
    fontWeight = FontWeight.Bold,
    fontSize = 35.sp, // Slightly larger to match header
    letterSpacing = 0.sp
)

// Placeholder inside search bar ("Search for Landmarks")
val SearchTextStyle = TextStyle(
    fontFamily = SearchFont,
    fontWeight = FontWeight.Normal,
    fontSize = 17.sp,
    letterSpacing = 0.25.sp,
    color = TextGray // Defined in Color.kt
)

// Labels under the images (Landmark name)
val LandmarkLabelTextStyle = TextStyle(
    fontFamily = LabelFont,
    fontWeight = FontWeight.SemiBold,
    fontSize = 10.sp,
    letterSpacing = 0.15.sp,
    lineHeight = 14.sp,
    color = TextBlack // Defined in Color.kt
)

// -------------------------------
// 3. Material Theme Typography Mapping
// -------------------------------
// This allows you to use MaterialTheme.typography.titleLarge etc. if you prefer
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    // Mapping your custom styles to Material slots for convenience
    titleLarge = CityTitleTextStyle,
    labelMedium = LandmarkLabelTextStyle,
    bodyMedium = SearchTextStyle
)