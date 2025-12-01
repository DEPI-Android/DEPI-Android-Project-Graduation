package com.hfad.egypttour.data.util

/**
 * A generic wrapper class that holds a value with its loading status.
 * 
 * This sealed class represents the state of an asynchronous operation:
 * - [Success]: Operation completed successfully with data
 * - [Error]: Operation failed with an exception and optional message
 * - [Loading]: Operation is in progress
 * 
 * @param T The type of data being wrapped
 */
sealed class Result<out T> {
    
    /**
     * Represents a successful result containing data.
     * @property data The successful result data
     */
    data class Success<out T>(val data: T) : Result<T>()
    
    /**
     * Represents a failed result with an exception and optional error message.
     * @property exception The exception that caused the failure
     * @property message Optional human-readable error message
     */
    data class Error(val exception: Exception, val message: String? = null) : Result<Nothing>()
    
    /**
     * Represents a loading state (operation in progress).
     */
    object Loading : Result<Nothing>()
    
    /**
     * Returns the data if this is a [Success], or null otherwise.
     */
    fun getOrNull(): T? {
        return when (this) {
            is Success -> data
            else -> null
        }
    }
    
    /**
     * Returns true if this is a [Success].
     */
    val isSuccess: Boolean
        get() = this is Success
    
    /**
     * Returns true if this is an [Error].
     */
    val isError: Boolean
        get() = this is Error
    
    /**
     * Returns true if this is [Loading].
     */
    val isLoading: Boolean
        get() = this is Loading
    
    /**
     * Returns a string representation of this Result for debugging.
     */
    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception, message=$message]"
            is Loading -> "Loading"
        }
    }
}
