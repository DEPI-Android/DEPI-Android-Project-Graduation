package com.hfad.egypttour.data.model

sealed class Result<out T> {

    // 1. Success State
    data class Success<out T>(val data: T) : Result<T>()

    // 2. Error State (Fixed typo: 'massage' -> 'message')
    data class Error(
        val exception: Throwable? = null, // Changed to Throwable to be more generic
        override val message: String? = null       // This holds the actual error text
    ) : Result<Nothing>()

    // 3. Loading State
    object Loading : Result<Nothing>()

    // --- Helper Properties ---

    // Safely gets data if Success, or null otherwise
    fun getOrNull(): T? {
        return when (this) {
            is Success -> data
            else -> null
        }
    }

    // FIX: This must be a computed property (using 'get()')
    // It checks if "this" is an Error, and if so, returns the message.
    open val message: String?
        get() = (this as? Error)?.message

    val isSuccess: Boolean
        get() = this is Success

    val isError: Boolean
        get() = this is Error

    val isLoading: Boolean
        get() = this is Loading
}