package com.hfad.egypttour.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // Helper to get current User ID
    private val currentUserId: String?
        get() = auth.currentUser?.uid

    suspend fun toggleFavorite(landmarkId: Int): Boolean {
        val uid = currentUserId ?: return false
        val userRef = db.collection("users").document(uid)

        // Run transaction to check current state and toggle
        return try {
            db.runTransaction { transaction ->
                val snapshot = transaction.get(userRef)
                val currentFavorites = snapshot.get("favorites") as? List<Long> ?: emptyList()

                // Firestore stores numbers as Long, convert to Int for comparison
                val isFavorite = currentFavorites.contains(landmarkId.toLong())

                if (isFavorite) {
                    // Remove if already favorite
                    transaction.update(userRef, "favorites", FieldValue.arrayRemove(landmarkId))
                    false // Return new state (not favorite)
                } else {
                    // Add if not favorite
                    transaction.update(userRef, "favorites", FieldValue.arrayUnion(landmarkId))
                    true // Return new state (is favorite)
                }
            }.await()
        } catch (e: Exception) {
            false
        }
    }

    suspend fun toggleSave(landmarkId: Int): Boolean {
        val uid = currentUserId ?: return false
        val userRef = db.collection("users").document(uid)

        return try {
            db.runTransaction { transaction ->
                val snapshot = transaction.get(userRef)
                val currentSaves = snapshot.get("saves") as? List<Long> ?: emptyList()
                val isSaved = currentSaves.contains(landmarkId.toLong())

                if (isSaved) {
                    transaction.update(userRef, "saves", FieldValue.arrayRemove(landmarkId))
                    false
                } else {
                    transaction.update(userRef, "saves", FieldValue.arrayUnion(landmarkId))
                    true
                }
            }.await()
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getUserFavorites(): List<Int> {
        val uid = currentUserId ?: return emptyList()
        val snapshot = db.collection("users").document(uid).get().await()
        return (snapshot.get("favorites") as? List<Long>)?.map { it.toInt() } ?: emptyList()
    }

    suspend fun getUserSaves(): List<Int> {
        val uid = currentUserId ?: return emptyList()
        val snapshot = db.collection("users").document(uid).get().await()
        return (snapshot.get("saves") as? List<Long>)?.map { it.toInt() } ?: emptyList()
    }
}