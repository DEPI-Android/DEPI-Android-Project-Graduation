package com.hfad.egypttour.data.api

import com.hfad.egypttour.data.api.model.WikiResponse
import com.hfad.egypttour.data.util.Constants
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Enhanced Wikipedia API service with multiple search strategies
 */
interface WikiApiService {

    /**
     * PRIMARY METHOD: Category-based search
     */
    @GET("w/api.php")
    suspend fun getCategoryMembers(
        @Query("action") action: String = "query",
        @Query("format") format: String = "json",
        @Query("generator") generator: String = "categorymembers",
        @Query("gcmtitle") categoryName: String,
        @Query("gcmtype") categoryType: String = "page",
        @Query("gcmlimit") limit: Int = Constants.DEFAULT_LANDMARK_LIMIT,
        @Query("gcmnamespace") namespace: String = "0", // Only main namespace (articles)
        @Query("prop") properties: String = "pageimages|images|coordinates|extracts|categories",
        @Query("pilimit") pageImageLimit: Int = Constants.DEFAULT_LANDMARK_LIMIT,
        @Query("pithumbsize") thumbnailSize: Int = Constants.THUMBNAIL_SIZE_PX,
        @Query("imlimit") imageLimit: Int = 10,
        @Query("exintro") extractIntroOnly: Boolean = true,
        @Query("explaintext") extractPlainText: Boolean = true,
        @Query("exlimit") extractLimit: Int = Constants.DEFAULT_LANDMARK_LIMIT
    ): WikiResponse

    /**
     * FALLBACK 1: Geographic search using coordinates
     */
    @GET("w/api.php")
    suspend fun searchByCoordinates(
        @Query("action") action: String = "query",
        @Query("format") format: String = "json",
        @Query("list") list: String = "geosearch",
        @Query("gscoord") coordinates: String,
        @Query("gsradius") radiusMeters: Int = 20000, // 20km default
        @Query("gslimit") limit: Int = 50, // Get more for filtering
        @Query("gsnamespace") namespace: String = "0", // Only articles
        @Query("gsprimary") primary: String = "all" // All pages with coordinates
    ): WikiResponse

    /**
     * FALLBACK 2: Text-based search
     */
    @GET("w/api.php")
    suspend fun searchByText(
        @Query("action") action: String = "query",
        @Query("format") format: String = "json",
        @Query("list") list: String = "search",
        @Query("srsearch") searchQuery: String,
        @Query("srlimit") limit: Int = 30,
        @Query("srnamespace") namespace: String = "0", // Only articles
        @Query("srwhat") what: String = "text",
        @Query("srprop") props: String = "snippet|titlesnippet|categorysnippet"
    ): WikiResponse

    /**
     * FALLBACK 3: Get page details by title
     */
    @GET("w/api.php")
    suspend fun getPagesByTitles(
        @Query("action") action: String = "query",
        @Query("format") format: String = "json",
        @Query("titles") titles: String,
        @Query("prop") properties: String = "pageimages|images|coordinates|extracts|categories",
        @Query("pilimit") pageImageLimit: Int = Constants.DEFAULT_LANDMARK_LIMIT,
        @Query("pithumbsize") thumbnailSize: Int = Constants.THUMBNAIL_SIZE_PX,
        @Query("imlimit") imageLimit: Int = 10,
        @Query("exintro") extractIntroOnly: Boolean = true,
        @Query("explaintext") extractPlainText: Boolean = true,
        @Query("exlimit") extractLimit: Int = Constants.DEFAULT_LANDMARK_LIMIT
    ): WikiResponse

    /**
     * HELPER: Get page details after getting page IDs from search
     */
    @GET("w/api.php")
    suspend fun getPagesByIds(
        @Query("action") action: String = "query",
        @Query("format") format: String = "json",
        @Query("pageids") pageIds: String,
        @Query("prop") properties: String = "pageimages|images|coordinates|extracts|categories",
        @Query("pilimit") pageImageLimit: Int = Constants.DEFAULT_LANDMARK_LIMIT,
        @Query("pithumbsize") thumbnailSize: Int = Constants.THUMBNAIL_SIZE_PX,
        @Query("imlimit") imageLimit: Int = 10,
        @Query("exintro") extractIntroOnly: Boolean = true,
        @Query("explaintext") extractPlainText: Boolean = true,
        @Query("exlimit") extractLimit: Int = Constants.DEFAULT_LANDMARK_LIMIT
    ): WikiResponse

    companion object
}