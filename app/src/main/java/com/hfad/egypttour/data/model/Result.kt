package com.hfad.egypttour.data.model

sealed class Result<out T> {
data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception, val massage :String? = null) : Result<Nothing>()
object Loading : Result<Nothing>()


    fun getOrNull(): T? {
        return when (this) {
            is Success -> data
            else -> null
        }
    }

    val isSuccess: Boolean
        get() = this is Success

    val isError: Boolean
        get() = this is Error

    val isLoading: Boolean
        get() = this is Loading


}
