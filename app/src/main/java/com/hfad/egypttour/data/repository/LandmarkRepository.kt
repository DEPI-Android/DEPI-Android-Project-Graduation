
// kotlin
package com.hfad.egypttour.data.repository

import android.util.Log
import com.hfad.egypttour.data.api.WikiApiService
import com.hfad.egypttour.data.api.model.WikiPageDto
import com.hfad.egypttour.data.model.Governorate
import com.hfad.egypttour.data.model.Result
import com.hfad.egypttour.data.model.LandMark
import com.hfad.egypttour.data.util.Constants
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

    private fun mapDtoToLandmark(dto: WikiPageDto, governorate: Governorate): LandMark? {
        val title = dto.title
        if (title.isNullOrBlank()) {
            return null
        }

        // --- UPDATED IMAGE LOGIC ---
        // If thumbnail is null, use empty string "". Do NOT return null.
        val imageUrl = dto.thumbnail?.source ?: ""
        // ---------------------------
        if (dto.thumbnail == null || dto.thumbnail.source.isBlank()) {
            return null // This deletes the landmark from the final list
        }
        val description = dto.extract
        if (description.isNullOrBlank()) {
            // We can optionally keep items without descriptions too,
            // but usually a title without info isn't useful.
            return null
        }

        val coordinates = dto.coordinates?.firstOrNull()

        return LandMark(
            id = dto.pageId ?: 0, // Handle nullable pageId safely
            name = title,
            description = description,
            imageUrl = imageUrl,
            lat = coordinates?.lat,
            lon = coordinates?.lon,
            governorate = governorate
        )
    }

    /* suspend fun getAllLandmarks(): Result<List<LandMark>> {
        return withContext(Dispatchers.IO) {
            try {
                val allLandmarks = mutableListOf<LandMark>()

                Governorate.entries.forEach { governorate ->
                    when (val result = getLandmarks(governorate)) {
                        is Result.Success -> allLandmarks.addAll(result.data)
                        is Result.Error -> {
                            Log.w(Constants.LOG_TAG, "Failed to fetch ${governorate.displayName}: ${result.message}")
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
     */
}
