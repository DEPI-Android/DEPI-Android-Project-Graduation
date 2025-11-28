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
    ),

    ISMAILIA(
        id = "ismailia",
        displayName = "Ismailia",
        wikiCategory = "Category:Tourist_attractions_in_Ismailia",
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e0/Ismailia_Corniche.jpg/640px-Ismailia_Corniche.jpg"
    ),

    SUEZ(
        id = "suez",
        displayName = "Suez",
        wikiCategory = "Category:Tourist_attractions_in_Suez",
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Suez_Canal_lighthouse.jpg/640px-Suez_Canal_lighthouse.jpg"
    ),

    NORTH_SINAI(
        id = "north_sinai",
        displayName = "North Sinai",
        wikiCategory = "Category:Tourist_attractions_in_North_Sinai",
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/6c/Saint_Catherine_Monastery.jpg/640px-Saint_Catherine_Monastery.jpg"
    ),

    SOUTH_SINAI(
        id = "south_sinai",
        displayName = "South Sinai",
        wikiCategory = "Category:Tourist_attractions_in_South_Sinai",
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/5/5f/Sharm_El_Sheikh_Beach.jpg/640px-Sharm_El_Sheikh_Beach.jpg"
    ),

    RED_SEA(
        id = "red_sea",
        displayName = "Red Sea",
        wikiCategory = "Category:Tourist_attractions_in_Red_Sea",
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/1/1e/Hurghada_Marina.jpg/640px-Hurghada_Marina.jpg"
    ),

    MATROUH(
        id = "matrouh",
        displayName = "Matruh",
        wikiCategory = "Category:Tourist_attractions_in_Matrouh",
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/8a/Siwa_Oasis.jpg/640px-Siwa_Oasis.jpg"
    ),

    QENA(
        id = "qena",
        displayName = "Qena",
        wikiCategory = "Category:Tourist_attractions_in_Qena",
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/4/41/Dendera_Temple.jpg/640px-Dendera_Temple.jpg"
    ),

    SOHAG(
        id = "sohag",
        displayName = "Sohag",
        wikiCategory = "Category:Tourist_attractions_in_Sohag",
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/9/99/Abydos_Temple.jpg/640px-Abydos_Temple.jpg"
    ),

    ASYUT(
        id = "asyut",
        displayName = "Asyut",
        wikiCategory = "Category:Tourist_attractions_in_Asyut",
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/35/Asyut_Barrage.jpg/640px-Asyut_Barrage.jpg"
    ),

    MINYA(
        id = "minya",
        displayName = "Minya",
        wikiCategory = "Category:Tourist_attractions_in_Minya",
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b8/Beni_Hassan_Tombs.jpg/640px-Beni_Hassan_Tombs.jpg"
    ),

    FAYOUM(
        id = "fayoum",
        displayName = "Fayoum",
        wikiCategory = "Category:Tourist_attractions_in_Fayoum",
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/2/22/Fayoum_Oasis.jpg/640px-Fayoum_Oasis.jpg"
    ),

    BENI_SUEF(
        id = "beni_suef",
        displayName = "Beni Suef",
        wikiCategory = "Category:Tourist_attractions_in_Beni_Suef",
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/7/7f/Beni_Suef_Nile.jpg/640px-Beni_Suef_Nile.jpg"
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