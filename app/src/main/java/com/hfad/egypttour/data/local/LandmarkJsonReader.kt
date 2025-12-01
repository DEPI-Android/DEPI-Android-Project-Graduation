package com.hfad.egypttour.data.local

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hfad.egypttour.data.model.LandMark
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class LandmarkJsonReader {
    
    private val gson = Gson()
    private val tag = "LandmarkJsonReader"
    
    /**
     * Load all landmarks from assets/landmarks.json
     */
    suspend fun loadLandmarksFromAssets(context: Context): List<LocalLandmark> = withContext(Dispatchers.IO) {
        try {
            val jsonString = context.assets.open("landmarks.json").bufferedReader().use { it.readText() }
            val type = object : TypeToken<List<LocalLandmark>>() {}.type
            gson.fromJson<List<LocalLandmark>>(jsonString, type)
        } catch (e: IOException) {
            Log.e(tag, "Error reading landmarks.json", e)
            emptyList()
        }
    }
    
    /**
     * Load image mappings from assets/landmark_images.json
     */
    suspend fun loadImageMappingsFromAssets(context: Context): LocalImageMapping? = withContext(Dispatchers.IO) {
        try {
            val jsonString = context.assets.open("landmark_images.json").bufferedReader().use { it.readText() }
            gson.fromJson(jsonString, LocalImageMapping::class.java)
        } catch (e: IOException) {
            Log.e(tag, "Error reading landmark_images.json", e)
            null
        }
    }
    
    /**
     * Merge landmarks with image paths and convert to app LandMark model
     */
    suspend fun loadCompleteLocalDatabase(context: Context): List<LandMark> = withContext(Dispatchers.IO) {
        val localLandmarks = loadLandmarksFromAssets(context)
        val imageMapping = loadImageMappingsFromAssets(context)
        
        val imageMap = imageMapping?.landmarks?.associateBy { it.id } ?: emptyMap()
        val result = mutableListOf<LandMark>()
        
        for (localLandmark in localLandmarks) {
            // Map governorate string to enum
            val governorate = GovernorateMapper.mapToAppGovernorate(localLandmark.governorate)
            
            if (governorate == null) {
                // Log warning but continue - these are expected for unsupported governorates
                // Log.w(tag, "Skipping landmark '${localLandmark.name}' - unmapped governorate: ${localLandmark.governorate}")
                continue
            }
            
            // Get images for this landmark
            val landmarkImages = imageMap[localLandmark.id]
            val imagePaths = landmarkImages?.images?.map { 
                "file:///android_asset/${it.relative_path}"
            } ?: emptyList()
            
            // Convert string ID to int
            val intId = IdMapper.getIntegerId(localLandmark.id)
            
            // Create LandMark object
            val landmark = LandMark(
                id = intId,
                name = localLandmark.name,
                description = localLandmark.description,
                imageUrl = imagePaths.firstOrNull(),  // First image as thumbnail
                imageUrls = imagePaths.drop(1),       // Rest of images for gallery
                lat = localLandmark.coordinates?.latitude,
                lon = localLandmark.coordinates?.longitude,
                governorate = governorate,
                localImagePaths = imagePaths
            )
            
            result.add(landmark)
        }
        
        Log.d(tag, "Loaded ${result.size} landmarks from local database (${localLandmarks.size - result.size} skipped)")
        result
    }
}
