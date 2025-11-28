package com.hfad.egypttour.data.api

import com.hfad.egypttour.data.api.model.WikiResponse
import com.hfad.egypttour.data.util.Constants
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Enhanced Wikipedia API service with multiple search strategies
 * for governorates without dedicated categories.
 */
interface WikiApiService {

    /**
     * PRIMARY METHOD: Category-based search
     * Works when a governorate has a dedicated Wikipedia category
     */
    @GET("w/api.php")
    suspend fun getCategoryMembers(
        @Query("action") action: String = "query",
        @Query("format") format: String = "json",
        @Query("generator") generator: String = "categorymembers",
        @Query("gcmtitle") categoryName: String,
        @Query("gcmtype") categoryType: String = "page",
        @Query("gcmlimit") limit: Int = Constants.DEFAULT_LANDMARK_LIMIT,
        @Query("prop") properties: String = "pageimages|images|coordinates|extracts",
        @Query("pilimit") pageImageLimit: Int = Constants.DEFAULT_LANDMARK_LIMIT,
        @Query("pithumbsize") thumbnailSize: Int = Constants.THUMBNAIL_SIZE_PX,
        @Query("imlimit") imageLimit: Int = 10,
        @Query("exintro") extractIntroOnly: Boolean = true,
        @Query("explaintext") extractPlainText: Boolean = true
    ): WikiResponse

    /**
     * FALLBACK 1: Geographic search using coordinates
     * Searches for pages within a radius of the governorate's center
     *
     * Example: Find all pages within 50km of Cairo's coordinates
     */
    @GET("w/api.php")
    suspend fun searchByCoordinates(
        @Query("action") action: String = "query",
        @Query("format") format: String = "json",
        @Query("list") list: String = "geosearch",
        @Query("gscoord") coordinates: String, // Format: "30.0444|31.2357" (lat|lon)
        @Query("gsradius") radiusMeters: Int = 50000, // 50km radius
        @Query("gslimit") limit: Int = Constants.DEFAULT_LANDMARK_LIMIT,
        @Query("gsprop") gsProp: String = "type"
    ): WikiResponse

    /**
     * FALLBACK 2: Text-based search
     * Searches Wikipedia for pages containing the governorate name + tourism keywords
     *
     * Example: "Faiyum tourism landmarks"
     */
    @GET("w/api.php")
    suspend fun searchByText(
        @Query("action") action: String = "query",
        @Query("format") format: String = "json",
        @Query("list") list: String = "search",
        @Query("srsearch") searchQuery: String,
        @Query("srlimit") limit: Int = Constants.DEFAULT_LANDMARK_LIMIT,
        @Query("srwhat") what: String = "text"
    ): WikiResponse

    /**
     * FALLBACK 3: Get page details by title
     * Fetch specific pages when you know their exact names
     *
     * Example: When you manually know "Wadi El Rayan" is in Faiyum
     */
    @GET("w/api.php")
    suspend fun getPagesByTitles(
        @Query("action") action: String = "query",
        @Query("format") format: String = "json",
        @Query("titles") titles: String, // Pipe-separated: "Title1|Title2|Title3"
        @Query("prop") properties: String = "pageimages|images|coordinates|extracts",
        @Query("pilimit") pageImageLimit: Int = Constants.DEFAULT_LANDMARK_LIMIT,
        @Query("pithumbsize") thumbnailSize: Int = Constants.THUMBNAIL_SIZE_PX,
        @Query("imlimit") imageLimit: Int = 10,
        @Query("exintro") extractIntroOnly: Boolean = true,
        @Query("explaintext") extractPlainText: Boolean = true
    ): WikiResponse

    /**
     * HELPER: Get page details after getting page IDs from search
     */
    @GET("w/api.php")
    suspend fun getPagesByIds(
        @Query("action") action: String = "query",
        @Query("format") format: String = "json",
        @Query("pageids") pageIds: String, // Pipe-separated IDs
        @Query("prop") properties: String = "pageimages|images|coordinates|extracts",
        @Query("pilimit") pageImageLimit: Int = Constants.DEFAULT_LANDMARK_LIMIT,
        @Query("pithumbsize") thumbnailSize: Int = Constants.THUMBNAIL_SIZE_PX,
        @Query("imlimit") imageLimit: Int = 10,
        @Query("exintro") extractIntroOnly: Boolean = true,
        @Query("explaintext") extractPlainText: Boolean = true
    ): WikiResponse
}