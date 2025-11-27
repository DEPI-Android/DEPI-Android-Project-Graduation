package com.hfad.egypttour.data.model

enum class Governorate(
    val id: String,
    val displayName: String,
    val wikiCategory: String,
    val imageUrl: String
) {
    CAIRO(
        id = "cairo",
        displayName = "Cairo",
        wikiCategory = "Category:Tourist_attractions_in_Cairo",
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/cd/Cairo_Skyline_2011.jpg/640px-Cairo_Skyline_2011.jpg"
    ),

    LUXOR(
        id = "luxor",
        displayName = "Luxor",
        wikiCategory = "Category:Tourist_attractions_in_Luxor",
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e3/Luxor_Temple_Egypt.jpg/640px-Luxor_Temple_Egypt.jpg"
    ),

    ASWAN(
        id = "aswan",
        displayName = "Aswan",
        wikiCategory = "Category:Tourist_attractions_in_Aswan",
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3b/Felucca_on_the_Nile_at_Aswan.jpg/640px-Felucca_on_the_Nile_at_Aswan.jpg"
    ),

    GIZA(
        id = "giza",
        displayName = "Giza",
        wikiCategory = "Category:Tourist_attractions_in_Giza",
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/af/All_Gizah_Pyramids.jpg/640px-All_Gizah_Pyramids.jpg"
    ),

    ALEXANDRIA(
        id = "alexandria",
        displayName = "Alexandria",
        wikiCategory = "Category:Tourist_attractions_in_Alexandria",
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f3/Alexandria_Citadel_of_Qaitbay.jpg/640px-Alexandria_Citadel_of_Qaitbay.jpg"
    );

    companion object {
        /**
         * Safely retrieves a Governorate by its ID string.
         * Returns null if not found (prevents crashes from invalid routing).
         */
        fun fromId(id: String): Governorate? {
            return Governorate.entries.find { it.id.equals(id, ignoreCase = true) }
        }
    }
}