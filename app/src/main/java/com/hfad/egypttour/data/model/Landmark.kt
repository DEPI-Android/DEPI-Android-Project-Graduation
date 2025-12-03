package com.hfad.egypttour.data.model

data class LandMark(
    val id: Int,
    val name: String,
    val description: String,
    val imageUrl: String?,
    val imageUrls: List<String> = emptyList(),
    val lat: Double?,
    val lon: Double?,
    val governorate: Governorate,
    val localImagePaths: List<String> = emptyList()
) {


    /**
     * Determines if this landmark needs Wikipedia description fallback
     * Returns true if:
     * - Description is null or blank
     * - Description has less than 15 words
     * - Description has less than 100 characters (approximately 2-3 lines on screen)
     */
    val needsWikipediaDescription: Boolean
        get() {
            if (description.isNullOrBlank()) return true
            
            // Check word count (existing logic)
            val wordCount = description.trim().split("\\s+".toRegex()).size
            if (wordCount < 15) return true
            
            // Check character count for visual length (2-3 lines ≈ 100 chars)
            if (description.length < 1200) return true
            
            return false
        }



}