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
     */
    val needsWikipediaDescription: Boolean
        get() {
            if (description.isNullOrBlank()) return true
            val wordCount = description.trim().split("\\s+".toRegex()).size
            return wordCount < 15
        }

//    val shortDescription: String
//        get() = if (description.length > 100) {
//            description.take(147) + "..."
//        } else {
//            description
//        }
//
//    val hasGallery: Boolean
//        get() = imageUrls.isNotEmpty()
//    val totalImages: Int
//        get() = 1 + imageUrls.size
//    val allImages: List<String>
//        get() = (listOf(imageUrl) + imageUrls) as List<String>


}