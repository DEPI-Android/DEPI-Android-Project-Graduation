package com.hfad.egypttour.data.util

object Constants {

    // api config
    const val WIKIPEDIA_BASE_URL = "https://en.wikipedia.org/"
    const val API_Endpoint = "w/api.php"


    // Request Configuration
    const val CONNECT_TIMEOUT_SECONDS = 30L
    const val READ_TIMEOUT_SECONDS = 30L
    const val WRITE_TIMEOUT_SECONDS = 30L

    // User-Agent (REQUIRED by Wikipedia)
    const val USER_AGENT = "EgyptTourApp/1.0 (Android; contact@egypttourapp.com)"

    // Query Limits
    const val DEFAULT_LANDMARK_LIMIT = 50
    const val THUMBNAIL_SIZE_PX = 500

    // Logging

    const val LOG_TAG = "EgyptTour"

}