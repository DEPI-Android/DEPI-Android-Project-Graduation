package com.hfad.egypttour.data.model

/**
 * User data model matching Firestore structure
 */
data class User(
    val username: String = "",
    val email: String = "",
    val favorites: List<Int> = emptyList(),
    val saves: List<Int> = emptyList()
)