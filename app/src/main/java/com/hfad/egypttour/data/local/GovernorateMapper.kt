package com.hfad.egypttour.data.local

import com.hfad.egypttour.data.model.Governorate

object GovernorateMapper {
    
    /**
     * Maps database governorate strings to app Governorate enum
     * Handles variations like "Cairo", "Cairo Governorate", etc.
     */
    fun mapToAppGovernorate(dbGovernorate: String): Governorate? {
        val normalized = dbGovernorate.trim().lowercase()
        
        return when {
            // Cairo variants
            normalized.contains("cairo") -> Governorate.CAIRO
            
            // Alexandria variants
            normalized.contains("alexandria") -> Governorate.ALEXANDRIA
            
            // Giza variants
            normalized.contains("giza") -> Governorate.GIZA
            
            // Luxor variants (includes "Thebes", "Karnak")
            normalized.contains("luxor") || 
            normalized.contains("thebes") || 
            normalized.contains("karnak") -> Governorate.LUXOR
            
            // Aswan variants
            normalized.contains("aswan") ||
            normalized.contains("elephantine") -> Governorate.ASWAN
            
            // Faiyum variants
            normalized.contains("faiyum") || 
            normalized.contains("fayoum") -> Governorate.FAIYUM
            
            // Port Said variants
            normalized.contains("port said") -> Governorate.PORT_SAID
            
            // Suez variants
            normalized.contains("suez") ||
            normalized.contains("ismailia") ||
            normalized.contains("el-qantarah") -> Governorate.SUEZ
            
            // Sharm El Sheikh / South Sinai variants
            normalized.contains("sharm") || 
            normalized.contains("south sinai") ||
            (normalized.contains("sinai") && !normalized.contains("north")) -> Governorate.SHARM_EL_SHEIKH
            
            // Hurghada / Red Sea variants
            normalized.contains("hurghada") ||
            normalized.contains("red sea") -> Governorate.HURGHADA
            
            // Explicitly exclude known non-Egyptian data errors in database
            normalized.contains("neukölln") || // Berlin
            normalized.contains("jenin") ||    // Palestine
            normalized.contains("banjul") ||   // Gambia
            normalized.contains("besançon") -> null
            
            // Unmapped governorates (e.g. Minya, Sohag) - return null
            // These will be skipped as they don't fit in the app's 10-governorate UI
            else -> {
                android.util.Log.w("GovernorateMapper", "Unmapped governorate: $dbGovernorate")
                null
            }
        }
    }
    
    /**
     * Get all database governorate strings mapped to a specific app Governorate
//     */
//    fun getVariantsFor(governorate: Governorate): List<String> {
//        return when (governorate) {
//            Governorate.CAIRO -> listOf("Cairo", "Cairo Governorate", "Nasr City", "Rhoda Island")
//            Governorate.ALEXANDRIA -> listOf("Alexandria", "Alexandria Governorate")
//            Governorate.GIZA -> listOf("Giza", "Giza Governorate", "Markaz al Badrashayn")
//            Governorate.LUXOR -> listOf("Luxor", "Luxor Governorate", "Thebes", "Karnak", "Dendera")
//            Governorate.ASWAN -> listOf("Aswan", "Aswan Governorate", "Elephantine")
//            Governorate.FAIYUM -> listOf("Faiyum Governorate", "Faiyum")
//            Governorate.PORT_SAID -> listOf("Port Said")
//            Governorate.SUEZ -> listOf("Suez Governorate", "Ismailia", "El-Qantarah al-Sharqiya")
//            Governorate.SHARM_EL_SHEIKH -> listOf("South Sinai Governorate", "Sharm El Sheikh")
//            Governorate.HURGHADA -> listOf("Red Sea Governorate", "Hurghada")
//        }
//    }
}
