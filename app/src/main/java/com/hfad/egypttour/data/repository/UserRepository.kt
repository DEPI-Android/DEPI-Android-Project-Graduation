package com.hfad.egypttour.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.hfad.egypttour.data.session.SessionManager
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for user-specific data (favorites, saves)
 * Stores data in Firestore under users/{userId}
 * Caches data locally via SessionManager for instant access
 */
@Singleton
class UserRepository @Inject constructor(
    private val sessionManager: SessionManager  // Injected for local caching
) {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    
    companion object {
        private const val TAG = "UserRepository"
    }

    // Helper to get current User ID
    private val currentUserId: String?
        get() = auth.currentUser?.uid

    // ==================== CACHE ACCESS (Instant) ====================

    /**
     * Get cached favorites (instant, no network)
     */
    fun getCachedFavorites(): List<Int> = sessionManager.getCachedFavorites()

    /**
     * Get cached saves (instant, no network)
     */
    fun getCachedSaves(): List<Int> = sessionManager.getCachedSaves()

    // ==================== FIRESTORE SYNC ====================

    /**
     * Sync favorites from Firestore to local cache
     */
    suspend fun syncFavoritesFromFirestore(): List<Int> {
        val uid = currentUserId ?: return emptyList()
        return try {
            val snapshot = db.collection("users").document(uid).get().await()
            val favorites = (snapshot.get("favorites") as? List<*>)
                ?.mapNotNull { (it as? Number)?.toInt() } ?: emptyList()
            sessionManager.cacheFavorites(favorites)
            Log.d(TAG, "Synced ${favorites.size} favorites from Firestore")
            favorites
        } catch (e: Exception) {
            Log.e(TAG, "syncFavoritesFromFirestore failed: ${e.message}")
            getCachedFavorites()  // Return cached on error
        }
    }

    /**
     * Sync saves from Firestore to local cache
     */
    suspend fun syncSavesFromFirestore(): List<Int> {
        val uid = currentUserId ?: return emptyList()
        return try {
            val snapshot = db.collection("users").document(uid).get().await()
            val saves = (snapshot.get("saves") as? List<*>)
                ?.mapNotNull { (it as? Number)?.toInt() } ?: emptyList()
            sessionManager.cacheSaves(saves)
            Log.d(TAG, "Synced ${saves.size} saves from Firestore")
            saves
        } catch (e: Exception) {
            Log.e(TAG, "syncSavesFromFirestore failed: ${e.message}")
            getCachedSaves()  // Return cached on error
        }
    }

    // ==================== TOGGLE OPERATIONS ====================

    /**
     * Toggle favorite status for a landmark
     * Updates local cache immediately, then syncs to Firestore
     */
    suspend fun toggleFavorite(landmarkId: Int): Boolean {
        val uid = currentUserId ?: run {
            Log.e(TAG, "No user logged in")
            return false
        }
        
        // Check current state from cache
        val isFavorite = getCachedFavorites().contains(landmarkId)
        val newState = !isFavorite
        
        // Update cache immediately (optimistic update)
        if (newState) {
            sessionManager.addFavoriteToCache(landmarkId)
        } else {
            sessionManager.removeFavoriteFromCache(landmarkId)
        }
        Log.d(TAG, "Cache updated: favorite=$newState for $landmarkId")
        
        // Sync to Firestore in background
        val userRef = db.collection("users").document(uid)
        return try {
            val snapshot = userRef.get().await()
            
            if (newState) {
                // Add to favorites
                if (!snapshot.contains("favorites")) {
                    userRef.set(mapOf("favorites" to listOf(landmarkId)), SetOptions.merge()).await()
                } else {
                    userRef.update("favorites", FieldValue.arrayUnion(landmarkId)).await()
                }
            } else {
                // Remove from favorites
                userRef.update("favorites", FieldValue.arrayRemove(landmarkId)).await()
            }
            Log.d(TAG, "Firestore synced: favorite=$newState for $landmarkId")
            newState
        } catch (e: Exception) {
            Log.e(TAG, "toggleFavorite Firestore sync failed: ${e.message}", e)
            // Cache is already updated, return expected state
            newState
        }
    }

    /**
     * Toggle save (bookmark) status for a landmark
     * Updates local cache immediately, then syncs to Firestore
     */
    suspend fun toggleSave(landmarkId: Int): Boolean {
        val uid = currentUserId ?: run {
            Log.e(TAG, "No user logged in")
            return false
        }
        
        // Check current state from cache
        val isSaved = getCachedSaves().contains(landmarkId)
        val newState = !isSaved
        
        // Update cache immediately (optimistic update)
        if (newState) {
            sessionManager.addSaveToCache(landmarkId)
        } else {
            sessionManager.removeSaveFromCache(landmarkId)
        }
        Log.d(TAG, "Cache updated: saved=$newState for $landmarkId")
        
        // Sync to Firestore in background
        val userRef = db.collection("users").document(uid)
        return try {
            val snapshot = userRef.get().await()
            
            if (newState) {
                // Add to saves
                if (!snapshot.contains("saves")) {
                    userRef.set(mapOf("saves" to listOf(landmarkId)), SetOptions.merge()).await()
                } else {
                    userRef.update("saves", FieldValue.arrayUnion(landmarkId)).await()
                }
            } else {
                // Remove from saves
                userRef.update("saves", FieldValue.arrayRemove(landmarkId)).await()
            }
            Log.d(TAG, "Firestore synced: saved=$newState for $landmarkId")
            newState
        } catch (e: Exception) {
            Log.e(TAG, "toggleSave Firestore sync failed: ${e.message}", e)
            // Cache is already updated, return expected state
            newState
        }
    }

    // ==================== LEGACY (for backward compatibility) ====================

    /**
     * Get user's favorited landmark IDs from Firestore
     * Prefer using getCachedFavorites() + syncFavoritesFromFirestore() instead
     */
    suspend fun getUserFavorites(): List<Int> = syncFavoritesFromFirestore()

    /**
     * Get user's saved landmark IDs from Firestore
     * Prefer using getCachedSaves() + syncSavesFromFirestore() instead
     */
    suspend fun getUserSaves(): List<Int> = syncSavesFromFirestore()
}