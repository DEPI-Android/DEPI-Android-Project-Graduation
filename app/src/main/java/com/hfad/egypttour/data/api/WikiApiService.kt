package com.hfad.egypttour.data.api
import com.hfad.egypttour.data.api.model.WikiResponse
import com.hfad.egypttour.data.util.Constants
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit interface for the Wikipedia MediaWiki API.
 *
 * This service uses the "generator" pattern to fetch category members
 * along with their images, coordinates, and text extracts in a SINGLE request.
 *
 * Key Wikipedia API concepts:
 * - generator=categorymembers: Gets all pages in a category
 * - prop=pageimages|coordinates|extracts: For each page, fetch these properties
 * - pilimit: REQUIRED when using pageimages with generators (bug in API)
 */
interface WikiApiService {

    /**
     * Fetches all landmark pages from a Wikipedia category along with their metadata.
     *
     * @param categoryName The full category name (e.g., "Category:Tourist_attractions_in_Cairo")
     * @return WikiResponse containing a map of page IDs to page data
     *
     * Example URL generated:
     * https://en.wikipedia.org/w/api.php?action=query&format=json
     * &generator=categorymembers&gcmtitle=Category:Tourist_attractions_in_Cairo
     * &gcmtype=page&gcmlimit=50&prop=pageimages|coordinates|extracts
     * &pilimit=50&pithumbsize=500&exintro=true&explaintext=true
     */
    @GET("w/api.php")
    suspend fun getCategoryMembers(
        // Core API parameters
        @Query("action") action: String = "query",
        @Query("format") format: String = "json",

        // Generator: Get all pages from a category
        @Query("generator") generator: String = "categorymembers",
        @Query("gcmtitle") categoryName: String,
        @Query("gcmtype") categoryType: String = "page",
        @Query("gcmlimit") limit: Int = Constants.DEFAULT_LANDMARK_LIMIT,

        // Properties: What data to fetch for each page
        @Query("prop") properties: String = "pageimages|coordinates|extracts",

        // Page Images configuration
        @Query("pilimit") pageImageLimit: Int = Constants.DEFAULT_LANDMARK_LIMIT, // CRITICAL: Required with generators
        @Query("pithumbsize") thumbnailSize: Int = Constants.THUMBNAIL_SIZE_PX,

        // Extracts configuration
        @Query("exintro") extractIntroOnly: Boolean = true,     // Only get intro paragraph
        @Query("explaintext") extractPlainText: Boolean = true  // Remove HTML formatting
    ): WikiResponse
}