package com.hfad.egypttour.data.util

object Constants {

    // api config
    const val WIKIPEDIA_BASE_URL = "https://en.wikipedia.org/"
    const val API_Endpoint = "w/api.php"


    // Request Configuration (Max 30 seconds total before Google Search fallback)
    // Connect timeout: 10s (establishing connection)
    const val CONNECT_TIMEOUT_SECONDS = 10L
    // Read timeout: 20s (reading response from Wikipedia)
    const val READ_TIMEOUT_SECONDS = 20L
    // Write timeout: 10s (sending request)
    const val WRITE_TIMEOUT_SECONDS = 10L

    // User-Agent (REQUIRED by Wikipedia)
    const val USER_AGENT = "EgyptTourApp/1.0 (Android; contact@egypttourapp.com)"

    // Query Limits
    const val DEFAULT_LANDMARK_LIMIT = 50
    const val THUMBNAIL_SIZE_PX = 500

    // Logging

    const val LOG_TAG = "EgyptTour"

}