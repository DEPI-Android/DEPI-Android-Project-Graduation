package com.hfad.egypttour.data.util

import com.hfad.egypttour.data.model.Governorate

object PlaceholderImages {

    /**
     * Returns a placeholder image URL based on the governorate.
     * Uses the governorate's cover image as fallback.
     */
    fun getPlaceholderForGovernorate(governorate: Governorate): String {
        return governorate.imageUrl
    }

    /**
     * Alternative: Get a generic "no image" placeholder
     */
    fun getGenericPlaceholder(): String {
        return "https://upload.wikimedia.org/wikipedia/commons/thumb/a/ac/No_image_available.svg/300px-No_image_available.svg.png"
    }

    /**
     * Validates if a URL is actually a valid image URL
     * (prevents using broken Wikipedia links)
     */
    fun isValidImageUrl(url: String?): Boolean {
        if (url.isNullOrBlank()) return false

        // Check if URL ends with common image extensions
        val imageExtensions = listOf(".jpg", ".jpeg", ".png", ".gif", ".webp", ".svg")
        return imageExtensions.any { url.lowercase().contains(it) }
    }
}