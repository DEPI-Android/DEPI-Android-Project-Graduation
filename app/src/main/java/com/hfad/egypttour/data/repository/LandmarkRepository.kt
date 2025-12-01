package com.hfad.egypttour.data.repository

import android.util.Log
import com.hfad.egypttour.data.api.WikiApiService
import com.hfad.egypttour.data.api.model.WikiPageDto
import com.hfad.egypttour.data.model.Governorate
import com.hfad.egypttour.data.util.Result
import com.hfad.egypttour.data.model.LandMark
import com.hfad.egypttour.data.util.Constants
import com.hfad.egypttour.data.util.PlaceholderImages
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import retrofit2.HttpException
import java.io.IOException
import java.security.MessageDigest
import kotlin.math.*

import android.content.Context
import com.hfad.egypttour.data.local.LandmarkJsonReader

class LandmarkRepository(
    private val apiService: WikiApiService,
    private val context: Context
) {
    private val jsonReader = LandmarkJsonReader()
    private var localLandmarksCache: List<LandMark>? = null

    /**
     * SMART FETCHING with STRICT FILTERING
     */
    // Cache fetched landmarks per governorate
//    private val cachedLandmarks = mutableMapOf<Governorate, List<LandMark>>()

    suspend fun getLandmarks(governorate: Governorate): Result<List<LandMark>> {
        // STEP 1: Load from local database first
        val localLandmarks = loadLocalLandmarksForGovernorate(governorate)
        
        if (localLandmarks.isNotEmpty()) {
            Log.d(Constants.LOG_TAG, "📦 Found ${localLandmarks.size} local landmarks for ${governorate.displayName}")
            return Result.Success(localLandmarks)
        }
        
        // STEP 2: Fallback to Wikipedia (only if local data missing)
        Log.d(Constants.LOG_TAG, "⚠️ No local landmarks for ${governorate.displayName}, falling back to Wikipedia")
        return fetchFromWikipedia(governorate)
    }

    private suspend fun fetchFromWikipedia(governorate: Governorate): Result<List<LandMark>> {
        val result = withContext(Dispatchers.IO) {
            try {
                Log.d(Constants.LOG_TAG, "🔍 Fetching landmarks for ${governorate.displayName}")

                val allResults = mutableListOf<LandMark>()

                // STRATEGY 1: Known landmarks FIRST (highest quality)
                if (governorate.knownLandmarks.isNotEmpty()) {
                    Log.d(
                        Constants.LOG_TAG,
                        "📋 Fetching ${governorate.knownLandmarks.size} known landmarks"
                    )
                    val knownResult = fetchKnownLandmarks(governorate)
                    if (knownResult.isNotEmpty()) {
                        Log.d(Constants.LOG_TAG, "✅ Found ${knownResult.size} known landmarks")
                        allResults.addAll(knownResult)
                    }
                }

                // STRATEGY 2: Category search (if available)
                if (governorate.hasCategory()) {
                    Log.d(Constants.LOG_TAG, "📂 Trying category: ${governorate.wikiCategory}")
                    val categoryResult = fetchByCategory(governorate)
                    if (categoryResult.isNotEmpty()) {
                        Log.d(
                            Constants.LOG_TAG,
                            "✅ Found ${categoryResult.size} landmarks via category"
                        )
                        allResults.addAll(categoryResult)
                    }
                }

                // STRATEGY 3: Geographic search (ONLY if we need more)
                if (allResults.size < 8) {
                    Log.d(
                        Constants.LOG_TAG,
                        "📍 Trying geographic search near ${governorate.getCoordinateString()}"
                    )
                    val geoResult = fetchByCoordinates(governorate)
                    if (geoResult.isNotEmpty()) {
                        Log.d(
                            Constants.LOG_TAG,
                            "✅ Found ${geoResult.size} landmarks via coordinates"
                        )
                        allResults.addAll(geoResult)
                    }
                }

                // Remove duplicates and apply strict filtering
                val filteredResults = allResults
                    .distinctBy { it.id }
                    .filter { isRelevantLandmark(it, governorate) }
                    .filter { isWithinGovernorate(it, governorate) }
                    .sortedByDescending { it.relevanceScore(governorate) }
                    .take(15) // Limit to top 15 most relevant

                Log.d(
                    Constants.LOG_TAG,
                    "✅ Final result: ${filteredResults.size} landmarks for ${governorate.displayName}"
                )

                if (filteredResults.isEmpty()) {
                    Log.w(Constants.LOG_TAG, "⚠️ No landmarks found for ${governorate.displayName}")
                }

                Result.Success(filteredResults)

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
        return result
    }

    // ============= STRATEGY IMPLEMENTATIONS =============

    /**
     * STRATEGY 1: Known landmarks (HIGHEST QUALITY)
     */
    private suspend fun fetchKnownLandmarks(governorate: Governorate): List<LandMark> {
        return try {
            val titles = governorate.knownLandmarks.joinToString("|")
            val response = apiService.getPagesByTitles(titles = titles)
            val pages = response.query?.pages ?: return emptyList()

            pages.values
                .mapNotNull { dto -> mapDtoToLandmark(dto, governorate) }
                .filter { it.name.isNotBlank() }

        } catch (e: Exception) {
            Log.w(Constants.LOG_TAG, "Known landmarks fetch failed: ${e.message}")
            emptyList()
        }
    }

    /**
     * STRATEGY 2: Category-based search with STRICT filtering
     */
    private suspend fun fetchByCategory(governorate: Governorate): List<LandMark> {
        val category = governorate.wikiCategory ?: return emptyList()

        return try {
            val response = apiService.getCategoryMembers(categoryName = category)
            val pages = response.query?.pages ?: return emptyList()

            pages.values
                .mapNotNull { dto -> mapDtoToLandmark(dto, governorate) }
                .filter { it.name.isNotBlank() && it.description.length >= 100 }

        } catch (e: Exception) {
            Log.w(Constants.LOG_TAG, "Category search failed: ${e.message}")
            emptyList()
        }
    }

    /**
     * STRATEGY 3: Geographic coordinate search with DISTANCE FILTERING
     */
    private suspend fun fetchByCoordinates(governorate: Governorate): List<LandMark> {
        return try {
            // Adjust radius based on governorate size
            val radius = when (governorate) {
                Governorate.CAIRO, Governorate.GIZA -> 15000 // 15km for dense cities
                Governorate.ALEXANDRIA -> 20000 // 20km
                Governorate.FAIYUM -> 30000 // 30km for large area
                else -> 20000 // 20km default
            }

            val geoResponse = apiService.searchByCoordinates(
                coordinates = governorate.getCoordinateString(),
                radiusMeters = radius // ✅ FIXED: correct parameter name
            )

            val pageIds = geoResponse.query?.geosearch
                ?.filter {
                    it.distance != null &&
                            it.distance < radius.toDouble() &&
                            !it.title.isNullOrBlank()
                }
                ?.mapNotNull { it.pageId }
                ?.take(30) // Get more initially for better filtering
                ?: return emptyList()

            if (pageIds.isEmpty()) {
                Log.d(Constants.LOG_TAG, "No geosearch results for ${governorate.displayName}")
                return emptyList()
            }

            val idsString = pageIds.joinToString("|")
            val detailsResponse = apiService.getPagesByIds(pageIds = idsString)
            val pages = detailsResponse.query?.pages ?: return emptyList()

            pages.values
                .mapNotNull { dto -> mapDtoToLandmark(dto, governorate) }
                .filter { landmark ->
                    landmark.lat != null &&
                            landmark.lon != null &&
                            landmark.description.length >= 80
                }

        } catch (e: Exception) {
            Log.w(Constants.LOG_TAG, "Geo search failed: ${e.message}", e)
            emptyList()
        }
    }

    // ============= FILTERING & VALIDATION =============

    /**
     * ULTRA STRICT RELEVANCE CHECK
     */
    private fun isRelevantLandmark(landmark: LandMark, governorate: Governorate): Boolean {
        val nameLower = landmark.name.lowercase()
        val descLower = landmark.description.lowercase()
        val govNameLower = governorate.displayName.lowercase()

        // 1. BLACKLIST: Exclude meta pages
        val blacklist = listOf(
            "wikipedia:", "template:", "category:", "portal:", "user:",
            "list of", "index of", "outline of", "timeline of",
            "history of $govNameLower", "geography of $govNameLower",
            "economy of $govNameLower", "demographics of $govNameLower",
            "climate of $govNameLower", "transport in $govNameLower",
            "education in", "health in", "politics of", "government of"
        )

        if (blacklist.any { nameLower.contains(it) || descLower.startsWith(it) }) {
            Log.d(Constants.LOG_TAG, "❌ Blacklisted: ${landmark.name}")
            return false
        }

        // 2. WHITELIST: Known landmarks always pass
        val isKnownLandmark = governorate.knownLandmarks.any {
            it.equals(landmark.name, ignoreCase = true)
        }
        if (isKnownLandmark) {
            Log.d(Constants.LOG_TAG, "✅ Known landmark: ${landmark.name}")
            return true
        }

        // 3. Must have substantial description
        if (landmark.description.length < 100) {
            Log.d(Constants.LOG_TAG, "❌ Short description: ${landmark.name}")
            return false
        }

        // 4. TOURISM KEYWORDS (strict)
        val tourismKeywords = listOf(
            "temple", "pyramid", "mosque", "church", "monastery", "synagogue",
            "museum", "palace", "fort", "fortress", "castle", "citadel",
            "monument", "memorial", "archaeological", "ancient", "historic",
            "tomb", "necropolis", "shrine", "sanctuary", "basilica",
            "park", "garden", "zoo", "aquarium", "botanical",
            "beach", "island", "bay", "resort", "coral", "reef",
            "tower", "lighthouse", "bridge", "dam", "canal",
            "obelisk", "sphinx", "statue", "ruins", "site", "complex",
            "theatre", "amphitheater", "stadium", "colosseum",
            "market", "bazaar", "souk", "square", "plaza"
        )

        val hasTourismKeyword = tourismKeywords.any {
            nameLower.contains(it) || descLower.contains(it)
        }

        // 5. Must mention governorate OR have tourism keyword
        val mentionsGovernorate = descLower.contains(govNameLower) ||
                nameLower.contains(govNameLower)

        if (!mentionsGovernorate && !hasTourismKeyword) {
            Log.d(Constants.LOG_TAG, "❌ Unrelated: ${landmark.name}")
            return false
        }

        // 6. EGYPT CONTEXT: Must mention Egypt or Egyptian
        val hasEgyptContext = descLower.contains("egypt") ||
                descLower.contains("egyptian") ||
                mentionsGovernorate ||
                isKnownLandmark

        if (!hasEgyptContext) {
            Log.d(Constants.LOG_TAG, "❌ Not Egyptian context: ${landmark.name}")
            return false
        }

        return true
    }

    /**
     * CHECK if landmark is geographically within governorate bounds
     */
    private fun isWithinGovernorate(landmark: LandMark, governorate: Governorate): Boolean {
        // If no coordinates, trust other validation
        if (landmark.lat == null || landmark.lon == null) {
            return true
        }

        val distance = calculateDistance(
            governorate.latitude, governorate.longitude,
            landmark.lat!!, landmark.lon!!
        )

        // Dynamic max distance based on governorate
        val maxDistance = when (governorate) {
            Governorate.CAIRO -> 25.0 // Dense city
            Governorate.GIZA -> 30.0 // Includes pyramids area
            Governorate.ALEXANDRIA -> 35.0 // Coastal spread
            Governorate.LUXOR -> 40.0 // East/West bank
            Governorate.ASWAN -> 50.0 // Includes Abu Simbel area
            Governorate.FAIYUM -> 60.0 // Large oasis area
            Governorate.SHARM_EL_SHEIKH, Governorate.HURGHADA -> 45.0 // Resort areas
            else -> 40.0
        }

        if (distance > maxDistance) {
            Log.d(
                Constants.LOG_TAG,
                "❌ Too far: ${landmark.name} is ${
                    String.format(
                        "%.1f",
                        distance
                    )
                }km away (max: $maxDistance km)"
            )
            return false
        }

        Log.d(
            Constants.LOG_TAG,
            "✅ Within bounds: ${landmark.name} is ${String.format("%.1f", distance)}km away"
        )
        return true
    }

    /**
     * Calculate distance between two coordinates (Haversine formula)
     */
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // Earth radius in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    /**
     * Calculate relevance score for sorting (0-200+ points)
     */
    private fun LandMark.relevanceScore(governorate: Governorate): Double {
        var score = 0.0

        // TIER 1: Known landmarks (100 points)
        val isKnownLandmark = governorate.knownLandmarks.any {
            it.equals(name, ignoreCase = true)
        }
        if (isKnownLandmark) {
            score += 100.0
            Log.d(Constants.LOG_TAG, "🏆 Known landmark bonus: $name (+100)")
        }

        // TIER 2: Has coordinates (30 points + distance bonus)
        if (lat != null && lon != null) {
            score += 30.0

            // Distance bonus: closer = higher score (max 50 points)
            val distance = calculateDistance(
                governorate.latitude, governorate.longitude,
                lat!!, lon!!
            )
            val distanceBonus = (50.0 - distance).coerceIn(0.0, 50.0)
            score += distanceBonus
        }

        // TIER 3: Name contains governorate (25 points)
        if (name.contains(governorate.displayName, ignoreCase = true)) {
            score += 25.0
        }

        // TIER 4: Description mentions governorate (20 points)
        if (description.contains(governorate.displayName, ignoreCase = true)) {
            score += 20.0
        }

        // TIER 5: Has quality image (15 points)
        if (imageUrl != null && !imageUrl.contains("placeholder", ignoreCase = true)) {
            score += 15.0
        }

        // TIER 6: Description quality (max 15 points)
        val descLengthBonus = (description.length / 100.0).coerceAtMost(15.0)
        score += descLengthBonus

        // TIER 7: Tourism keywords (10 points per keyword, max 30)
        val premiumKeywords = listOf(
            "pyramid", "temple", "tomb", "palace", "museum",
            "archaeological", "ancient", "pharaoh", "unesco"
        )
        val keywordMatches = premiumKeywords.count {
            name.contains(it, ignoreCase = true) ||
                    description.contains(it, ignoreCase = true)
        }
        score += (keywordMatches * 10.0).coerceAtMost(30.0)

        // TIER 8: Has image gallery (10 points)
        if (imageUrls.isNotEmpty()) {
            score += 10.0
        }

        // TIER 9: Name mentions tourism/landmark (5 points)
        val landmarkTerms = listOf("national", "historic", "royal", "grand", "great")
        if (landmarkTerms.any { name.contains(it, ignoreCase = true) }) {
            score += 5.0
        }

        return score
    }

    // ============= HELPER FUNCTIONS =============

    /**
     * Map WikiPageDto to LandMark with validation
     */
    private fun mapDtoToLandmark(dto: WikiPageDto, governorate: Governorate): LandMark? {
        // Validate title
        val title = dto.title?.trim()
        if (title.isNullOrBlank()) {
            Log.w(Constants.LOG_TAG, "⚠️ Skipping page ${dto.pageId}: No title")
            return null
        }

        // Validate description
        val description = dto.extract?.trim()
        if (description.isNullOrBlank()) {
            Log.w(Constants.LOG_TAG, "⚠️ Skipping '$title': No description")
            return null
        }

        // Skip if description is too short (likely stub article)
        if (description.length < 50) {
            Log.w(
                Constants.LOG_TAG,
                "⚠️ Skipping '$title': Description too short (${description.length} chars)"
            )
            return null
        }

        // Handle image.. skip the item if there is no image.
        val primaryImageUrl = if (PlaceholderImages.isValidImageUrl(dto.thumbnail?.source)) {
            dto.thumbnail!!.source
        } else {
            Log.d(Constants.LOG_TAG, "Skipping '$title': No thumbnail image")
            return null
        }

        // Extract additional images
        val additionalImages = extractAdditionalImages(dto, primaryImageUrl)
        if (additionalImages.isNotEmpty()) {
            Log.d(Constants.LOG_TAG, "🖼️ '$title' has ${additionalImages.size} additional images")
        }

        // Extract coordinates
        val coordinates = dto.coordinates?.firstOrNull()
        if (coordinates == null) {
            Log.d(Constants.LOG_TAG, "📍 '$title' has no coordinates (acceptable)")
        } else {
            Log.d(Constants.LOG_TAG, "📍 '$title' located at ${coordinates.lat}, ${coordinates.lon}")
        }

        return LandMark(
            id = dto.pageId ?: 0,
            name = title,
            description = description,
            imageUrl = primaryImageUrl,
            lat = coordinates?.lat,
            lon = coordinates?.lon,
            imageUrls = additionalImages,
            governorate = governorate
        )
    }

    /**
     * Construct proper Wikimedia Commons image URL
     */
//    private fun constructImageUrl(fileTitle: String): String? {
//        try {
//            // Remove "File:" prefix
//            val filename = fileTitle.removePrefix("File:").trim()
//            if (filename.isBlank()) return null
//
//            // Skip non-image files
//            val imageExtensions = listOf(".jpg", ".jpeg", ".png", ".gif", ".svg", ".webp")
//            if (!imageExtensions.any { filename.lowercase().endsWith(it) }) {
//                return null
//            }
//
//            // Normalize filename
//            val normalizedFilename = filename.replace(" ", "_")
//            val firstChar = normalizedFilename.first().lowercaseChar()
//            val secondChar = normalizedFilename.getOrNull(1)?.lowercaseChar() ?: firstChar
//
//            // Construct URL
//            return "https://upload.wikimedia.org/wikipedia/commons/thumb/$firstChar/$firstChar$secondChar/$normalizedFilename/500px-$normalizedFilename"
//        } catch (e: Exception) {
//            Log.w(Constants.LOG_TAG, "⚠️ Failed to construct image URL for: $fileTitle", e)
//            return null
//        }
//    }


    // ----------------------------------------------------------
    // HELPER: IMAGE URL CONSTRUCTION (THE FIX) 🛠️
    // ----------------------------------------------------------

    private fun extractAdditionalImages(dto: WikiPageDto, primaryImageUrl: String?): List<String> {
        val images = dto.images ?: return emptyList()

        return images
            .mapNotNull { imageInfo -> constructImageUrl(imageInfo.title) }
            .filter { url ->
                // Remove duplicates and non-photos
                url != primaryImageUrl &&
                        !url.contains("icon", ignoreCase = true) &&
                        !url.contains("logo", ignoreCase = true) &&
                        !url.contains("flag", ignoreCase = true) &&
                        !url.contains(".svg", ignoreCase = true)
            }
            .distinct()
            .take(8)
    }

    /**
     * Converts "File:Name.jpg" to a valid Wikimedia CDN URL using MD5 hashing.
     */
    private fun constructImageUrl(fileTitle: String): String? {
        try {
            // 1. Clean the filename
            val filename = fileTitle.removePrefix("File:").trim().replace(" ", "_")
            if (filename.isBlank()) return null

            // 2. Filter extensions (Keep only photos)
            val imageExtensions = listOf(".jpg", ".jpeg", ".png", ".webp")
            if (!imageExtensions.any { filename.lowercase().endsWith(it) }) {
                return null
            }

            // 3. Calculate MD5 Hash
            val hash = md5(filename)
            val a = hash.substring(0, 1)
            val ab = hash.substring(0, 2)

            // 4. Build URL
            // Format: https://upload.wikimedia.org/wikipedia/commons/thumb/a/ab/Filename.jpg/640px-Filename.jpg
            return "https://upload.wikimedia.org/wikipedia/commons/thumb/$a/$ab/$filename/640px-$filename"

        } catch (e: Exception) {
            Log.e(Constants.LOG_TAG, "Failed to construct URL for $fileTitle", e)
            return null
        }
    }

    // MD5 Calculation Helper
    private fun md5(input: String): String {
        val bytes = MessageDigest.getInstance("MD5").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }




    /**
     * Extract additional images from page, excluding primary
     */
//    private fun extractAdditionalImages(dto: WikiPageDto, primaryImageUrl: String?): List<String> {
//        val images = dto.images ?: return emptyList()
//
//        return images
//            .mapNotNull { imageInfo -> constructImageUrl(imageInfo.title) }
//            .filter { url ->
//                // Exclude primary image and invalid URLs
//                url != primaryImageUrl &&
//                        PlaceholderImages.isValidImageUrl(url) &&
//                        !url.contains("icon", ignoreCase = true) &&
//                        !url.contains("logo", ignoreCase = true) &&
//                        !url.contains("flag", ignoreCase = true)
//            }
//            .distinct() // Remove duplicates
//            .take(6) // Limit to 6 additional images
//    }

    /**
     * Get all landmarks from all governorates
     */
    suspend fun getAllLandmarks(): Result<List<LandMark>> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(Constants.LOG_TAG, "🌍 Fetching landmarks for all governorates")
                val allLandmarks = mutableListOf<LandMark>()
                val errors = mutableListOf<String>()

                Governorate.entries.forEach { governorate ->
                    when (val result = getLandmarks(governorate)) {
                        is Result.Success -> {
                            allLandmarks.addAll(result.data)
                            Log.d(
                                Constants.LOG_TAG,
                                "✅ ${governorate.displayName}: ${result.data.size} landmarks"
                            )
                        }

                        is Result.Error -> {
                            val errorMsg = "${governorate.displayName}: ${result.message}"
                            errors.add(errorMsg)
                            Log.w(Constants.LOG_TAG, "⚠️ $errorMsg")
                        }

                        is Result.Loading -> { /* No-op */
                        }
                    }
                }

                // Log summary
                val totalCount = allLandmarks.size
                val uniqueCount = allLandmarks.distinctBy { it.id }.size
                Log.d(Constants.LOG_TAG, "📊 Total: $totalCount landmarks ($uniqueCount unique)")

                if (errors.isNotEmpty()) {
                    Log.w(Constants.LOG_TAG, "⚠️ Errors in ${errors.size} governorates")
                }

                // Return unique landmarks only
                Result.Success(allLandmarks.distinctBy { it.id })

            } catch (e: Exception) {
                val errorMsg = "Failed to fetch all landmarks: ${e.message}"
                Log.e(Constants.LOG_TAG, errorMsg, e)
                Result.Error(e, errorMsg)
            }
        }
    }

    /**
     * Search landmarks across all governorates by query
     */
    suspend fun searchLandmarks(query: String): Result<List<LandMark>> {
        return withContext(Dispatchers.IO) {
            try {
                if (query.isBlank()) {
                    return@withContext Result.Success(emptyList<LandMark>())
                }

                Log.d(Constants.LOG_TAG, "🔍 Searching for: $query")

                // First, try to get all landmarks
                val allLandmarksResult = getAllLandmarks()

                if (allLandmarksResult is Result.Success) {
                    val queryLower = query.lowercase().trim()

                    // Filter landmarks matching the query
                    val matchedLandmarks = allLandmarksResult.data.filter { landmark ->
                        landmark.name.lowercase().contains(queryLower) ||
                                landmark.description.lowercase().contains(queryLower) ||
                                landmark.governorate.displayName.lowercase().contains(queryLower)
                    }.sortedByDescending { landmark ->
                        // Prioritize name matches over description matches
                        when {
                            landmark.name.lowercase() == queryLower -> 100.0
                            landmark.name.lowercase().startsWith(queryLower) -> 80.0
                            landmark.name.lowercase().contains(queryLower) -> 60.0
                            landmark.governorate.displayName.lowercase()
                                .contains(queryLower) -> 40.0

                            landmark.description.lowercase().contains(queryLower) -> 20.0
                            else -> 0.0
                        }
                    }

                    Log.d(
                        Constants.LOG_TAG,
                        "✅ Found ${matchedLandmarks.size} matches for '$query'"
                    )
                    Result.Success(matchedLandmarks)
                } else {
                    Result.Error(Exception("Failed to fetch landmarks for search"), "Search failed")
                }

            } catch (e: Exception) {
                val errorMsg = "Search failed: ${e.message}"
                Log.e(Constants.LOG_TAG, errorMsg, e)
                Result.Error(e, errorMsg)
            }
        }
    }



    suspend fun getLandmarkById(id: Int): Result<LandMark?> = withContext(Dispatchers.IO) {
        // STEP 1: Search in local database
        val localLandmark = findLocalLandmarkById(id)
        
        if (localLandmark != null) {
            // STEP 2: Check if description is null, empty, or has less than 15 words
            val description = localLandmark.description
            val wordCount = if (description.isNullOrBlank()) 0 else description.trim().split("\\s+".toRegex()).size
            
            if (localLandmark.needsWikipediaDescription) {
                Log.d(Constants.LOG_TAG, "📝 Landmark '${localLandmark.name}': Description needs Wikipedia fallback (words: $wordCount)")
                
                // STEP 3: Fetch detailed description from Wikipedia
                val wikiDescription = fetchWikipediaDescription(localLandmark.name)
                
                // Merge: Use Wikipedia if available, otherwise keep local (even if empty)
                val enhancedDescription = if (wikiDescription.isNotBlank()) {
                    Log.d(Constants.LOG_TAG, "✅ Wikipedia description fetched successfully (${wikiDescription.split("\\s+".toRegex()).size} words)")
                    wikiDescription
                } else {
                    Log.w(Constants.LOG_TAG, "⚠️ Wikipedia fetch failed, keeping local description")
                    localLandmark.description
                }
                
                return@withContext Result.Success(localLandmark.copy(description = enhancedDescription))
            }
            
            // Description is sufficient (≥15 words), return as-is
            Log.d(Constants.LOG_TAG, "✅ Landmark '${localLandmark.name}': Description sufficient ($wordCount words), using local data")
            return@withContext Result.Success(localLandmark)
        }
        
        // STEP 4: Not in local DB, fallback to full Wikipedia fetch
        Log.w(Constants.LOG_TAG, "Landmark $id not found in local database, fetching from Wikipedia")
        
        try {
            Log.d(Constants.LOG_TAG, "🔍 Fetching landmark with ID: $id")

            val allLandmarksResult = getAllLandmarks()

            if (allLandmarksResult is Result.Success) {
                val landmark = allLandmarksResult.data.find { it.id == id }

                if (landmark != null) {
                    Log.d(Constants.LOG_TAG, "✅ Found landmark: ${landmark.name}")
                } else {
                    Log.w(Constants.LOG_TAG, "⚠️ No landmark found with ID: $id")
                }

                return@withContext Result.Success(landmark)
            } else {
                return@withContext Result.Error(Exception("Failed to fetch landmarks"), "Fetch failed")
            }

        } catch (e: Exception) {
            val errorMsg = "Failed to get landmark by ID: ${e.message}"
            Log.e(Constants.LOG_TAG, errorMsg, e)
            return@withContext Result.Error(e, errorMsg)
        }
    }

    // ============= LOCAL DATA HELPERS =============

    private suspend fun getLocalLandmarks(): List<LandMark> {
        if (localLandmarksCache == null) {
            localLandmarksCache = jsonReader.loadCompleteLocalDatabase(context)
        }
        return localLandmarksCache ?: emptyList()
    }

    private suspend fun loadLocalLandmarksForGovernorate(governorate: Governorate): List<LandMark> {
        return getLocalLandmarks().filter { it.governorate == governorate }
    }

    private suspend fun findLocalLandmarkById(id: Int): LandMark? {
        return getLocalLandmarks().find { it.id == id }
    }

    /**
     * Fetches Wikipedia description for a landmark with timeout and proper error handling.
     * Runs on IO dispatcher to prevent NetworkOnMainThreadException on physical devices.
     */
    private suspend fun fetchWikipediaDescription(landmarkName: String): String {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(Constants.LOG_TAG, "🌍 Fetching Wikipedia description for: $landmarkName")
                
                // Add 20-second timeout to prevent indefinite waiting
                withTimeout(20000L) {
                    val response = apiService.getPagesByTitles(titles = landmarkName)
                    val page = response.query?.pages?.values?.firstOrNull()
                    val extract = page?.extract ?: ""
                    
                    if (extract.isBlank()) {
                        Log.w(Constants.LOG_TAG, "⚠️ Wikipedia returned empty description for $landmarkName")
                    } else {
                        Log.d(Constants.LOG_TAG, "✅ Wikipedia description fetched (${extract.length} chars)")
                    }
                    
                    extract
                }
            } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
                Log.w(Constants.LOG_TAG, "⏱️ Wikipedia fetch TIMED OUT after 20s for $landmarkName")
                ""
            } catch (e: Exception) {
                Log.e(Constants.LOG_TAG, "❌ Wikipedia fetch FAILED for $landmarkName: ${e.javaClass.simpleName} - ${e.message}", e)
                ""
            }
        }
    }
}