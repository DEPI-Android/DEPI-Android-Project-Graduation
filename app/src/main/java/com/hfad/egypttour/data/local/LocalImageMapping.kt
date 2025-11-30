package com.hfad.egypttour.data.local

data class LocalImageMapping(
    val metadata: ImageMetadata,
    val landmarks: List<LandmarkImages>
)

data class ImageMetadata(
    val total_landmarks: Int,
    val total_images: Int,
    val landmarks_with_images: Int,
    val landmarks_without_images: Int,
    val max_images_per_landmark: Int?
)

data class LandmarkImages(
    val id: String,
    val name: String,
    val governorate: String,
    val folder_name: String,
    val image_directory: String,
    val image_count: Int,
    val images: List<ImageInfo>,
    val has_images: Boolean
)

data class ImageInfo(
    val filename: String,
    val path: String,
    val relative_path: String
)
