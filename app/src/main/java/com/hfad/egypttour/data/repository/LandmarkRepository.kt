
// kotlin
package com.hfad.egypttour.data.repository

import android.util.Log
import com.hfad.egypttour.data.api.WikiApiService
import com.hfad.egypttour.data.api.model.WikiPageDto
import com.hfad.egypttour.data.model.Governorate
import com.hfad.egypttour.data.model.Result
import com.hfad.egypttour.data.model.LandMark
import com.hfad.egypttour.data.util.Constants
import com.hfad.egypttour.data.util.PlaceholderImages
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class LandmarkRepository(
    private val apiService: WikiApiService
) {

    //    /**
//     * Fetches all landmarks for a given governorate from Wikipedia.
//     *
//     * @param governorate The governorate to fetch landmarks for
//     * @return Result.Success with landmarks, or Result.Error on failure
//     *
//     * Quality filters applied:
//     * - Must have a valid title
//     * - Must have a thumbnail image
//     * - Must have a description (even if short)
//     *
//     * Note: Landmarks without coordinates are INCLUDED (many museums lack GPS data)
//     */
    suspend fun getLandmarks(governorate: Governorate): Result<List<LandMark>> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(Constants.LOG_TAG, "Fetching landmarks for ${governorate.displayName}")

                val response = apiService.getCategoryMembers(
                    categoryName = governorate.wikiCategory
                )

                // Ensure we have a typed map so Kotlin can infer types downstream
                val pages = response.query?.pages

                if (pages.isNullOrEmpty()) {
                    Log.w(Constants.LOG_TAG, "No pages returned for ${governorate.displayName}")
                    return@withContext Result.Success<List<LandMark>>(emptyList())
                }

                Log.d(Constants.LOG_TAG, "Received ${pages.size} pages from Wikipedia")

                val landmarks = pages.values
                    .mapNotNull { dto -> mapDtoToLandmark(dto, governorate) }
                    .sortedByDescending { it.lat != null && it.lon != null } // prioritize those with coords

                Log.d(Constants.LOG_TAG, "Successfully mapped ${landmarks.size} landmarks")

                Result.Success<List<LandMark>>(landmarks)

            } catch (e: HttpException) {
                val errorMsg =
                    "HTTP ${e.code()}: Failed to fetch landmarks for ${governorate.displayName}"
                Log.e(Constants.LOG_TAG, errorMsg, e)
                Result.Error(e, errorMsg)

            } catch (e: IOException) {
                val errorMsg = "Network error: Unable to reach Wikipedia"
                Log.e(Constants.LOG_TAG, errorMsg, e)
                Result.Error(e, errorMsg)

            } catch (e: Exception) {
                val errorMsg = "Unexpected error: ${e.message}"
                Log.e(Constants.LOG_TAG, errorMsg, e)
                Result.Error(e, errorMsg)
            }
        }
    }

    private fun mapDtoToLandmark(dto: WikiPageDto, governorate: Governorate): LandMark? {
        val title = dto.title
        if (title.isNullOrBlank()) {
            Log.w(Constants.LOG_TAG, "Skipping page ${dto.pageId}: No title")
            return null
        }
// i want to put a place holder image if there is no thumbnail  <-----------------------------------
        val primaryImageUrl = if (PlaceholderImages.isValidImageUrl(dto.thumbnail?.source)) {
            dto.thumbnail!!.source
        } else {
            Log.d(Constants.LOG_TAG, "'$title' has no thumbnail, using placeholder")
            PlaceholderImages.getPlaceholderForGovernorate(governorate)
        }

        // NEW: Additional images for gallery (extract from images prop)
        val additionalImages = extractAdditionalImages(dto, primaryImageUrl)

        if (additionalImages.isNotEmpty()) {
            Log.d(Constants.LOG_TAG, "'$title' has ${additionalImages.size} additional images")
        }


        val description = dto.extract
        if (description.isNullOrBlank()) {
            Log.w(Constants.LOG_TAG, "Skipping '$title': No description")
            return null
        }

        val coordinates = dto.coordinates?.firstOrNull()
        if (coordinates == null) {
            Log.d(Constants.LOG_TAG, "'$title' has no coordinates (this is okay)")
        }

        // Use 'source' for thumbnail image; change to 'url' if your DTO exposes that field
        val imageUrl = dto.thumbnail?.source

        return LandMark(
            id = dto.pageId,
            name = title,
            description = description,
            imageUrl = imageUrl,
            lat = coordinates?.lat,
            lon = coordinates?.lon,
            imageUrls = additionalImages,
            governorate = governorate
        )
    }


    private fun constructImageUrl(fileTitle: String): String? {
        try {
            // Remove "File:" prefix
            val filename = fileTitle.removePrefix("File:").trim()

            if (filename.isBlank()) return null

            // Replace spaces with underscores (Wikipedia convention)
            val normalizedFilename = filename.replace(" ", "_")

            // Calculate MD5 hash for the Commons URL structure
            // (Simplified: we just use the first 2 characters of the filename as directory structure)
            val firstChar = normalizedFilename.first().lowercaseChar()
            val secondChar = normalizedFilename.getOrNull(1)?.lowercaseChar() ?: firstChar

            // Construct Wikimedia Commons URL
            // Format: https://upload.wikimedia.org/wikipedia/commons/thumb/{char1}/{char1}{char2}/{filename}/500px-{filename}
            return "https://upload.wikimedia.org/wikipedia/commons/thumb/$firstChar/$firstChar$secondChar/$normalizedFilename/500px-$normalizedFilename"
        } catch (e: Exception) {
            Log.w(Constants.LOG_TAG, "Failed to construct image URL for: $fileTitle", e)
            return null
        }
    }

    private fun extractAdditionalImages(dto: WikiPageDto, primaryImageUrl: String?): List<String> {
        val images = dto.images ?: return emptyList()

        return images
            .mapNotNull { imageInfo ->
                // Convert "File:Cairo_Skyline.jpg" to actual Wikimedia Commons URL
                constructImageUrl(imageInfo.title)
            }
            .filter { url ->
                // Exclude the primary thumbnail (avoid duplicates)
                url != primaryImageUrl && PlaceholderImages.isValidImageUrl(url)
            }
            .take(6)  // Limit to 6 additional images (performance)
    }

    suspend fun getAllLandmarks1(): Result<List<LandMark>> {
        return withContext(Dispatchers.IO) {
            try {
                val allLandmarks = mutableListOf<LandMark>()

                Governorate.entries.forEach { governorate ->
                    when (val result = getLandmarks(governorate)) {
                        is Result.Success -> allLandmarks.addAll(result.data)
                        is Result.Error -> {
                            Log.w(
                                Constants.LOG_TAG,
                                "Failed to fetch ${governorate.displayName}: ${result.massage}"
                            )
                        }

                        is Result.Loading -> { /* No-op */
                        }
                    }
                }

                Result.Success(allLandmarks)

            } catch (e: Exception) {
                val errorMsg = "Failed to fetch all landmarks: ${e.message}"
                Log.e(Constants.LOG_TAG, errorMsg, e)
                Result.Error(e, errorMsg)
            }
        }
    }

    suspend fun getAllLandmarks(): Result<List<LandMark>> {
        return withContext(Dispatchers.IO) {
            try {
                val allLandmarks = mutableListOf<LandMark>()

                Governorate.entries.forEach { governorate ->
                    when (val result = getLandmarks(governorate)) {
                        is Result.Success -> allLandmarks.addAll(result.data)
                        is Result.Error -> {
                            Log.w(
                                Constants.LOG_TAG,
                                "Failed to fetch ${governorate.displayName}: ${result.massage}"
                            )
                        }

                        is Result.Loading -> { /* No-op */
                        }
                    }
                }

                Result.Success<List<LandMark>>(allLandmarks)

            } catch (e: Exception) {
                val errorMsg = "Failed to fetch all landmarks: ${e.message}"
                Log.e(Constants.LOG_TAG, errorMsg, e)
                Result.Error(e, errorMsg)
            }
        }
    }

}