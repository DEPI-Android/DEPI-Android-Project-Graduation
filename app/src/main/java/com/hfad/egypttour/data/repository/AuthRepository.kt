package com.hfad.egypttour.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hfad.egypttour.data.model.User
import com.hfad.egypttour.data.session.SessionManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for authentication operations
 * Handles Firebase Auth, session management, and multi-device logout
 */
@Singleton
class AuthRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sessionManager: SessionManager
) {
    // Firebase instances
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    companion object {
        private const val TAG = "AuthRepository"
    }

    /**
     * Get current authenticated user's UID
     */
    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    /**
     * Check if user is authenticated
     */
    fun isAuthenticated(): Boolean {
        return auth.currentUser != null
    }

    /**
     * Fetch user data from Firestore
     * Refreshes auth token first to ensure valid authentication
     */
    suspend fun getUserData(userId: String): Result<User> {
        return try {
            Log.d(TAG, "=== Starting getUserData for userId: $userId ===")
            
            // Refresh Firebase Auth token to ensure it's valid
            val currentUser = auth.currentUser
            if (currentUser == null) {
                Log.e(TAG, "ERROR: currentUser is null!")
                return Result.failure(Exception("User not authenticated"))
            }
            
            Log.d(TAG, "Current user email: ${currentUser.email}, uid: ${currentUser.uid}")

            // Force token refresh to ensure we're authenticated
            try {
                Log.d(TAG, "Attempting to refresh auth token...")
                currentUser.getIdToken(true).await()
                Log.d(TAG, "✅ Auth token refreshed successfully")
            } catch (tokenError: Exception) {
                Log.e(TAG, "❌ Failed to refresh auth token: ${tokenError.message}", tokenError)
                return Result.failure(Exception("Authentication failed. Please login again."))
            }

            // Check network connectivity before fetching
            Log.d(TAG, "Checking network connectivity...")
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            val activeNetwork = connectivityManager?.activeNetwork
            val networkCapabilities = connectivityManager?.getNetworkCapabilities(activeNetwork)
            val hasInternet = networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
            val hasValidated = networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true
            
            Log.d(TAG, "Network status: hasInternet=$hasInternet, hasValidated=$hasValidated")
            Log.d(TAG, "Active network: $activeNetwork")
            Log.d(TAG, "Network capabilities: $networkCapabilities")

            // Now fetch user data from Firestore
            // Use Source.SERVER to bypass watch stream and fetch directly from server
            Log.d(TAG, "Fetching user document from Firestore...")
            val documentSnapshot = db.collection("users")
                .document(userId)
                .get(com.google.firebase.firestore.Source.SERVER)  // ← Force server fetch
                .await()

            Log.d(TAG, "Document exists: ${documentSnapshot.exists()}")
            
            if (documentSnapshot.exists()) {
                val user = documentSnapshot.toObject(User::class.java)
                if (user != null) {
                    Log.d(TAG, "✅ User data fetched successfully: ${user.username}")
                    Result.success(user)
                } else {
                    Log.e(TAG, "❌ Failed to parse user data")
                    Result.failure(Exception("Failed to parse user data"))
                }
            } else {
                Log.e(TAG, "❌ User document does not exist in Firestore!")
                Result.failure(Exception("User data not found in database. Please contact support."))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error fetching user data: ${e.javaClass.simpleName}: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Sign out user from THIS DEVICE ONLY
     * Clears Firebase Auth session and all local cached data
     */
    fun signOut() {
        Log.d(TAG, "Signing out from this device only")
        auth.signOut()
        sessionManager.clearAllUserData()  // Clears login session + cached profile
    }

// will be done in the future next update
    suspend fun signOutFromAllDevices(): Result<Unit> {
        return try {
            val userId = getCurrentUserId()
            if (userId == null) {
                return Result.failure(Exception("No user logged in"))
            }

            Log.d(TAG, "Logout from all devices requested for user: $userId")
            
            // TODO: Implement Cloud Function call
            // For now, just sign out locally
            signOut()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to logout from all devices", e)
            Result.failure(e)
        }
    }

    /**
     * Save login session (for "Remember me" feature)
     */
//    fun saveLoginSession() {
//        sessionManager.saveLoginSession()
//    }
//
//    /**
//     * Check if session exists in SharedPreferences
//     */
//    fun hasSession(): Boolean {
//        return sessionManager.isLoggedIn()
//    }
}
