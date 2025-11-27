package com.hfad.egypttour.data.model

data class LandMark(
    val id: Int?,
    val name: String,
    val description: String,
    val imageUrl: String,
    val lat: Double?,
    val lon: Double?,
    val governorate: Governorate


) {
    val haseCoordinates: Boolean
        get() = lat != null && lon != null


    val shortDescription: String
        get() = if (description.length > 100) {
            description.take(147) + "..."
        } else {
            description
        }

}