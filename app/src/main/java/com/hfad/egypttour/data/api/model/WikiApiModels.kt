package com.hfad.egypttour.data.api.model

import com.google.gson.annotations.SerializedName

/**
 * Root response from Wikipedia API
 * Handles all response types: category, geosearch, and text search
 */
data class WikiResponse(
    @SerializedName("query") val query: WikiQuery?
)

/**
 * Query object containing different result types
 */
data class WikiQuery(
    @SerializedName("pages") val pages: Map<String, WikiPageDto>?,
    @SerializedName("geosearch") val geosearch: List<GeoSearchResult>?,
    @SerializedName("search") val search: List<TextSearchResult>?
)

/**
 * Complete page data with all properties
 * This is the ONLY WikiPageDto definition - remove any other duplicates
 */
data class WikiPageDto(
    @SerializedName("pageid") val pageId: Int,
    @SerializedName("title") val title: String?,
    @SerializedName("extract") val extract: String?,
    @SerializedName("thumbnail") val thumbnail: ThumbnailDto?,
    @SerializedName("coordinates") val coordinates: List<CoordinateDto>?,
    @SerializedName("images") val images: List<ImageInfoDto>?
)

data class ThumbnailDto(
    @SerializedName("source") val source: String?,
    @SerializedName("width") val width: Int?,
    @SerializedName("height") val height: Int?
)

data class CoordinateDto(
    @SerializedName("lat") val lat: Double?,
    @SerializedName("lon") val lon: Double?,
    @SerializedName("primary") val primary: String?,
    @SerializedName("globe") val globe: String?
)

data class ImageInfoDto(
    @SerializedName("title") val title: String
)

/**
 * Geographic search result (for coordinate-based queries)
 */
data class GeoSearchResult(
    @SerializedName("pageid") val pageId: Int?,
    @SerializedName("title") val title: String?,
    @SerializedName("lat") val lat: Double?,
    @SerializedName("lon") val lon: Double?,
    @SerializedName("dist") val distance: Double?, // Distance in meters from search point
    @SerializedName("primary") val primary: String?
)

/**
 * Text search result (for keyword-based queries)
 */
data class TextSearchResult(
    @SerializedName("pageid") val pageId: Int?,
    @SerializedName("title") val title: String?,
    @SerializedName("snippet") val snippet: String?, // HTML snippet of match
    @SerializedName("timestamp") val timestamp: String?
)