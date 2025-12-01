package com.hfad.egypttour.data.util

data class LocalLandmark(
    val id: String,
    val name: String,
    val description: String,
    val governorate: String,
    val coordinates: Coordinates?,
    val imagePath: String?,
    val wikiUrl: String?
)

data class Coordinates(
    val latitude: Double,
    val longitude: Double
)
