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
    val imageUrl: String,  // Image URL for the governorate
    val knownLandmarks: List<String> = emptyList() // Manual fallback
) {
    CAIRO(
        id = "cairo",
        displayName = "Cairo",
        wikiCategory = "Category:Tourist_attractions_in_Cairo",
        latitude = 30.0444,
        longitude = 31.2357,
        imageUrl = "https://images.unsplash.com/photo-1572252009286-268acec5ca0a?w=800&q=80", // Cairo cityscape with mosques
        knownLandmarks = emptyList()
    ),

    ALEXANDRIA(
        id = "alexandria",
        displayName = "Alexandria",
        wikiCategory = "Category:Tourist_attractions_in_Alexandria",
        latitude = 31.2001,
        longitude = 29.9187,
        imageUrl = "https://images.unsplash.com/photo-1553913861-c0fddf2619ee?w=800&q=80", // Alexandria coastline
        knownLandmarks = emptyList()
    ),

    GIZA(
        id = "giza",
        displayName = "Giza",
        wikiCategory = "Category:Buildings_and_structures_in_Giza",
        latitude = 30.0131,
        longitude = 31.2089,
        imageUrl = "https://images.unsplash.com/photo-1568322445389-f64ac2515020?w=800&q=80", // Great Pyramids of Giza
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
        imageUrl = "https://images.unsplash.com/photo-1539768942893-daf53e448371?w=800&q=80", // Luxor Temple columns
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
        imageUrl = "https://images.unsplash.com/photo-1590068213685-586146781e63?w=800&q=80", // Nile at Aswan with feluccas
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
        imageUrl = "https://images.unsplash.com/photo-1473496169904-658ba7c44d8a?w=800&q=80", // Desert oasis/waterfall
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
        imageUrl = "https://images.unsplash.com/photo-1606840574311-ba25cb63b6be?w=800&q=80", // Port/harbor view
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
        imageUrl = "https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=800&q=80", // Suez Canal ships
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
        imageUrl = "https://images.unsplash.com/photo-1583771063440-8e628178a045?w=800&q=80", // Red Sea underwater/coral
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
        imageUrl = "https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800&q=80", // Red Sea beach resort
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