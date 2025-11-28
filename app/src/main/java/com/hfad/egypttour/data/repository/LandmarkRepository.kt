package com.hfad.egypttour.data.repository
import com.hfad.egypttour.data.api.model.*
import com.hfad.egypttour.data.api.model.WikiResponse
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

    /**
     * SMART FETCHING: Tries multiple strategies in order:
     * 1. Category search (if available)
     * 2. Geographic coordinate search
     * 3. Text search with governorate name
     * 4. Known landmarks (manual fallback)
     */
    suspend fun getLandmarks(governorate: Governorate): Result<List<LandMark>> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(Constants.LOG_TAG, "🔍 Fetching landmarks for ${governorate.displayName}")

                // STRATEGY 1: Try category search first (if category exists)
                if (governorate.hasCategory()) {
                    Log.d(Constants.LOG_TAG, "📂 Trying category: ${governorate.wikiCategory}")
                    val categoryResult = fetchByCategory(governorate)
                    if (categoryResult.isNotEmpty()) {
                        Log.d(Constants.LOG_TAG, "✅ Found ${categoryResult.size} landmarks via category")
                        return@withContext Result.Success(categoryResult)
                    }
                }

                // STRATEGY 2: Try geographic search
                Log.d(Constants.LOG_TAG, "📍 Trying geographic search near ${governorate.getCoordinateString()}")
                val geoResult = fetchByCoordinates(governorate)
                if (geoResult.isNotEmpty()) {
                    Log.d(Constants.LOG_TAG, "✅ Found ${geoResult.size} landmarks via coordinates")
                    return@withContext Result.Success(geoResult)
                }

                // STRATEGY 3: Try text search
                Log.d(Constants.LOG_TAG, "🔎 Trying text search for ${governorate.displayName}")
                val textResult = fetchByTextSearch(governorate)
                if (textResult.isNotEmpty()) {
                    Log.d(Constants.LOG_TAG, "✅ Found ${textResult.size} landmarks via text search")
                    return@withContext Result.Success(textResult)
                }

                // STRATEGY 4: Fallback to known landmarks
                if (governorate.knownLandmarks.isNotEmpty()) {
                    Log.d(Constants.LOG_TAG, "📋 Using ${governorate.knownLandmarks.size} known landmarks")
                    val knownResult = fetchKnownLandmarks(governorate)
                    if (knownResult.isNotEmpty()) {
                        Log.d(Constants.LOG_TAG, "✅ Found ${knownResult.size} known landmarks")
                        return@withContext Result.Success(knownResult)
                    }
                }

                // All strategies failed
                Log.w(Constants.LOG_TAG, "❌ No landmarks found for ${governorate.displayName}")
                Result.Success<List<LandMark>>(emptyList())

            } catch (e: HttpException) {
                val errorMsg = "HTTP ${e.code()}: Failed to fetch landmarks for ${governorate.displayName}"
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

    // ============= STRATEGY IMPLEMENTATIONS =============

    /**
     * STRATEGY 1: Category-based search
     */
    private suspend fun fetchByCategory(governorate: Governorate): List<LandMark> {
        val category = governorate.wikiCategory ?: return emptyList()

        return try {
            val response = apiService.getCategoryMembers(categoryName = category)
            val pages = response.query?.pages ?: return emptyList()

            pages.values
                .mapNotNull { dto -> mapDtoToLandmark(dto, governorate) }
                .sortedByDescending { it.lat != null && it.lon != null }
        } catch (e: Exception) {
            Log.w(Constants.LOG_TAG, "Category search failed: ${e.message}")
            emptyList()
        }
    }

    /**
     * STRATEGY 2: Geographic coordinate search
     */
    private suspend fun fetchByCoordinates(governorate: Governorate): List<LandMark> {
        return try {
            // First, get page IDs near the coordinates
            val geoResponse = apiService.searchByCoordinates(
                coordinates = governorate.getCoordinateString()
            )

            val pageIds = geoResponse.query?.geosearch
                ?.mapNotNull { it.pageId }
                ?.take(Constants.DEFAULT_LANDMARK_LIMIT)
                ?: return emptyList()

            if (pageIds.isEmpty()) return emptyList()

            // Then fetch full details for those page IDs
            val idsString = pageIds.joinToString("|")
            val detailsResponse = apiService.getPagesByIds(pageIds = idsString)
            val pages = detailsResponse.query?.pages ?: return emptyList()

            pages.values
                .mapNotNull { dto -> mapDtoToLandmark(dto, governorate) }
                .filter { isRelevantLandmark(it, governorate) }

        } catch (e: Exception) {
            Log.w(Constants.LOG_TAG, "Geo search failed: ${e.message}")
            emptyList()
        }
    }

    /**
     * STRATEGY 3: Text-based search
     */
    private suspend fun fetchByTextSearch(governorate: Governorate): List<LandMark> {
        return try {
            val searchQuery = "${governorate.displayName} tourism landmarks Egypt"
            val searchResponse = apiService.searchByText(searchQuery = searchQuery)

            val pageIds = searchResponse.query?.search
                ?.mapNotNull { it.pageId }
                ?.take(Constants.DEFAULT_LANDMARK_LIMIT)
                ?: return emptyList()

            if (pageIds.isEmpty()) return emptyList()

            // Fetch full details
            val idsString = pageIds.joinToString("|")
            val detailsResponse = apiService.getPagesByIds(pageIds = idsString)
            val pages = detailsResponse.query?.pages ?: return emptyList()

            pages.values
                .mapNotNull { dto -> mapDtoToLandmark(dto, governorate) }
                .filter { isRelevantLandmark(it, governorate) }

        } catch (e: Exception) {
            Log.w(Constants.LOG_TAG, "Text search failed: ${e.message}")
            emptyList()
        }
    }

    /**
     * STRATEGY 4: Known landmarks (manual fallback)
     */
    private suspend fun fetchKnownLandmarks(governorate: Governorate): List<LandMark> {
        return try {
            val titles = governorate.knownLandmarks.joinToString("|")
            val response = apiService.getPagesByTitles(titles = titles)
            val pages = response.query?.pages ?: return emptyList()

            pages.values
                .mapNotNull { dto -> mapDtoToLandmark(dto, governorate) }

        } catch (e: Exception) {
            Log.w(Constants.LOG_TAG, "Known landmarks fetch failed: ${e.message}")
            emptyList()
        }
    }

    // ============= HELPER FUNCTIONS =============

    /**
     * Filters out irrelevant results (e.g., Wikipedia meta pages)
     */
    private fun isRelevantLandmark(landmark: LandMark, governorate: Governorate): Boolean {
        val name = landmark.name.lowercase()

        // Exclude Wikipedia meta pages
        if (name.contains("wikipedia:") ||
            name.contains("template:") ||
            name.contains("category:") ||
            name.contains("portal:")) {
            return false
        }

        // Exclude very short descriptions (likely not real landmarks)
        if (landmark.description.length < 50) {
            return false
        }

        return true
    }

    private fun mapDtoToLandmark(dto: WikiPageDto, governorate: Governorate): LandMark? {
        val title = dto.title
        if (title.isNullOrBlank()) {
            Log.w(Constants.LOG_TAG, "Skipping page ${dto.pageId}: No title")
            return null
        }

        // Use placeholder if no thumbnail
        val primaryImageUrl = if (PlaceholderImages.isValidImageUrl(dto.thumbnail?.source)) {
            dto.thumbnail!!.source
        } else {
            Log.d(Constants.LOG_TAG, "'$title' has no thumbnail, using placeholder")
            PlaceholderImages.getPlaceholderForGovernorate(governorate)
        }

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
            val filename = fileTitle.removePrefix("File:").trim()
            if (filename.isBlank()) return null

            val normalizedFilename = filename.replace(" ", "_")
            val firstChar = normalizedFilename.first().lowercaseChar()
            val secondChar = normalizedFilename.getOrNull(1)?.lowercaseChar() ?: firstChar

            return "https://upload.wikimedia.org/wikipedia/commons/thumb/$firstChar/$firstChar$secondChar/$normalizedFilename/500px-$normalizedFilename"
        } catch (e: Exception) {
            Log.w(Constants.LOG_TAG, "Failed to construct image URL for: $fileTitle", e)
            return null
        }
    }

    private fun extractAdditionalImages(dto: WikiPageDto, primaryImageUrl: String?): List<String> {
        val images = dto.images ?: return emptyList()

        return images
            .mapNotNull { imageInfo -> constructImageUrl(imageInfo.title) }
            .filter { url ->
                url != primaryImageUrl && PlaceholderImages.isValidImageUrl(url)
            }
            .take(6)
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
                        is Result.Loading -> { /* No-op */ }
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

