//package com.hfad.egypttour.data.model
//
///**
// * Enhanced Governorate enum with multiple data sources:
// * - Wikipedia categories (primary)
// * - Geographic coordinates (fallback)
// * - Known landmark names (manual fallback)
// */
//enum class Governorate(
//    val id: String,
//    val displayName: String,
//    val wikiCategory: String?,  // NULL if no category exists
//    val latitude: Double,
//    val longitude: Double,
//    val knownLandmarks: List<String> = emptyList() // Manual fallback
//) {
//    CAIRO(
//        id = "cairo",
//        displayName = "Cairo",
//        wikiCategory = "Category:Tourist_attractions_in_Cairo",
//        latitude = 30.0444,
//        longitude = 31.2357
//    ),
//
//    ALEXANDRIA(
//        id = "alexandria",
//        displayName = "Alexandria",
//        wikiCategory = "Category:Tourist_attractions_in_Alexandria",
//        latitude = 31.2001,
//        longitude = 29.9187
//    ),
//
//    GIZA(
//        id = "giza",
//        displayName = "Giza",
//        wikiCategory = "Category:Buildings_and_structures_in_Giza",
//        latitude = 30.0131,
//        longitude = 31.2089,
//        knownLandmarks = listOf(
//            "Great Pyramid of Giza",
//            "Sphinx",
//            "Pyramid of Khafre",
//            "Pyramid of Menkaure"
//        )
//    ),
//
//    LUXOR(
//        id = "luxor",
//        displayName = "Luxor",
//        wikiCategory = "Category:Buildings_and_structures_in_Luxor",
//        latitude = 25.6872,
//        longitude = 32.6396,
//        knownLandmarks = listOf(
//            "Karnak Temple",
//            "Luxor Temple",
//            "Valley of the Kings",
//            "Valley of the Queens",
//            "Hatshepsut Temple"
//        )
//    ),
//
//    ASWAN(
//        id = "aswan",
//        displayName = "Aswan",
//        wikiCategory = "Category:Buildings_and_structures_in_Aswan",
//        latitude = 24.0889,
//        longitude = 32.8998,
//        knownLandmarks = listOf(
//            "Abu Simbel",
//            "Philae Temple",
//            "Aswan High Dam",
//            "Unfinished Obelisk"
//        )
//    ),
//
//    FAIYUM(
//        id = "faiyum",
//        displayName = "Faiyum",
//        wikiCategory = null, // NO CATEGORY - Will use fallbacks
//        latitude = 29.3084,
//        longitude = 30.8428,
//        knownLandmarks = listOf(
//            "Wadi El Rayan",
//            "Lake Qarun",
//            "Hawara Pyramid",
//            "Lahun Pyramid",
//            "Karanis"
//        )
//    ),
//
//    PORT_SAID(
//        id = "port_said",
//        displayName = "Port Said",
//        wikiCategory = null, // NO CATEGORY
//        latitude = 31.2653,
//        longitude = 32.3019,
//        knownLandmarks = listOf(
//            "Port Said Lighthouse",
//            "Military Museum",
//            "Port Said National Museum"
//        )
//    ),
//
//    SUEZ(
//        id = "suez",
//        displayName = "Suez",
//        wikiCategory = null, // NO CATEGORY
//        latitude = 29.9668,
//        longitude = 32.5498,
//        knownLandmarks = listOf(
//            "Suez Canal",
//            "Ahmed Hamdi Tunnel"
//        )
//    ),
//
//    SHARM_EL_SHEIKH(
//        id = "sharm_el_sheikh",
//        displayName = "Sharm El Sheikh",
//        wikiCategory = null, // NO CATEGORY
//        latitude = 27.9158,
//        longitude = 34.3300,
//        knownLandmarks = listOf(
//            "Ras Muhammad National Park",
//            "Naama Bay",
//            "Nabq Protected Area"
//        )
//    ),
//
//    HURGHADA(
//        id = "hurghada",
//        displayName = "Hurghada",
//        wikiCategory = null, // NO CATEGORY
//        latitude = 27.2579,
//        longitude = 33.8116,
//        knownLandmarks = listOf(
//            "Giftun Island",
//            "Hurghada Grand Aquarium",
//            "Makadi Bay"
//        )
//    );
//
//    companion object {
//        fun fromId(id: String): Governorate? {
//            return entries.find { it.id.equals(id, ignoreCase = true) }
//        }
//    }
//
//    /**
//     * Returns true if this governorate has a Wikipedia category
//     */
//    fun hasCategory(): Boolean = wikiCategory != null
//
//    /**
//     * Returns coordinate string for API: "lat|lon"
//     */
//    fun getCoordinateString(): String = "$latitude|$longitude"
//
//    /**
//     * Generates search queries for text-based fallback
//     */
//    fun getSearchQueries(): List<String> = listOf(
//        "$displayName tourism Egypt",
//        "$displayName landmarks",
//        "$displayName attractions Egypt",
//        "Places to visit in $displayName"
//    )
//}

package com.hfad.egypttour.data.model
import com.hfad.egypttour.R
import androidx.annotation.DrawableRes

/**
 * Enhanced Governorate enum with multiple data sources:
 * - Wikipedia categories (primary)
 * - Geographic coordinates (fallback)
 * - Known landmark names (manual fallback)
 * - Image URLs for visual representation
 */
enum class Governorate(
    val id: String,
    val displayName: String,
    val wikiCategory: String?,  // NULL if no category exists
    val latitude: Double,
    val longitude: Double,
    @DrawableRes val imageRes: Int,  // CHANGED: Now uses a local Resource ID
    val knownLandmarks: List<String> = emptyList() // Manual fallback
) {
    CAIRO(
        id = "cairo",
        displayName = "Cairo",
        wikiCategory = "Category:Tourist_attractions_in_Cairo",
        latitude = 30.0444,
        longitude = 31.2357,
        imageRes = R.drawable.cairo,
        knownLandmarks = emptyList()
    ),

    ALEXANDRIA(
        id = "alexandria",
        displayName = "Alexandria",
        wikiCategory = "Category:Tourist_attractions_in_Alexandria",
        latitude = 31.2001,
        longitude = 29.9187,
        imageRes = R.drawable.alex,
        knownLandmarks = emptyList()
    ),

    GIZA(
        id = "giza",
        displayName = "Giza",
        wikiCategory = "Category:Buildings_and_structures_in_Giza",
        latitude = 30.0131,
        longitude = 31.2089,
        imageRes = R.drawable.giza,
        knownLandmarks = listOf(
            "Great Pyramid of Giza",
            "Sphinx",
            "Pyramid of Khafre",
            "Pyramid of Menkaure"
        )
    ),

    LUXOR(
        id = "luxor",
        displayName = "Luxor",
        wikiCategory = "Category:Buildings_and_structures_in_Luxor",
        latitude = 25.6872,
        longitude = 32.6396,
        imageRes = R.drawable.luxor,
        knownLandmarks = listOf(
            "Karnak Temple",
            "Luxor Temple",
            "Valley of the Kings",
            "Valley of the Queens",
            "Hatshepsut Temple"
        )
    ),

    ASWAN(
        id = "aswan",
        displayName = "Aswan",
        wikiCategory = "Category:Buildings_and_structures_in_Aswan",
        latitude = 24.0889,
        longitude = 32.8998,
        imageRes = R.drawable.aswan,
        knownLandmarks = listOf(
            "Abu Simbel",
            "Philae Temple",
            "Aswan High Dam",
            "Unfinished Obelisk"
        )
    ),

    FAIYUM(
        id = "faiyum",
        displayName = "Faiyum",
        wikiCategory = null, // NO CATEGORY - Will use fallbacks
        latitude = 29.3084,
        longitude = 30.8428,
        imageRes = R.drawable.faiyum,
        knownLandmarks = listOf(
            "Wadi El Rayan",
            "Lake Qarun",
            "Hawara Pyramid",
            "Lahun Pyramid",
            "Karanis"
        )
    ),

    PORT_SAID(
        id = "port_said",
        displayName = "Port Said",
        wikiCategory = null, // NO CATEGORY
        latitude = 31.2653,
        longitude = 32.3019,
        imageRes = R.drawable.portsaid,
        knownLandmarks = listOf(
            "Port Said Lighthouse",
            "Military Museum",
            "Port Said National Museum"
        )
    ),

    SUEZ(
        id = "suez",
        displayName = "Suez",
        wikiCategory = null, // NO CATEGORY
        latitude = 29.9668,
        longitude = 32.5498,
        imageRes = R.drawable.suez,
        knownLandmarks = listOf(
            "Suez Canal",
            "Ahmed Hamdi Tunnel"
        )
    ),

    SHARM_EL_SHEIKH(
        id = "sharm_el_sheikh",
        displayName = "Sharm El Sheikh",
        wikiCategory = null, // NO CATEGORY
        latitude = 27.9158,
        longitude = 34.3300,
        imageRes = R.drawable.sharm,
        knownLandmarks = listOf(
            "Ras Muhammad National Park",
            "Naama Bay",
            "Nabq Protected Area"
        )
    ),

    HURGHADA(
        id = "hurghada",
        displayName = "Hurghada",
        wikiCategory = null, // NO CATEGORY
        latitude = 27.2579,
        longitude = 33.8116,
        imageRes = R.drawable.hurghada,
        knownLandmarks = listOf(
            "Giftun Island",
            "Hurghada Grand Aquarium",
            "Makadi Bay"
        )
    );

    companion object {
        fun fromId(id: String): Governorate? {
            return entries.find { it.id.equals(id, ignoreCase = true) }
        }
    }

    /**
     * Returns true if this governorate has a Wikipedia category
     */
    fun hasCategory(): Boolean = wikiCategory != null

    /**
     * Returns coordinate string for API: "lat|lon"
     */
    fun getCoordinateString(): String = "$latitude|$longitude"

    /**
     * Generates search queries for text-based fallback
     */
    fun getSearchQueries(): List<String> = listOf(
        "$displayName tourism Egypt",
        "$displayName landmarks",
        "$displayName attractions Egypt",
        "Places to visit in $displayName"
    )
}