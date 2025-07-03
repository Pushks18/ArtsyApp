// model/FavoriteRequest.kt
package com.example.artsyapp.model

data class FavoriteRequest(
    val artistName: String,
    val artistImageURL: String,
    val birthYear: Int?,
    val deathYear: Int?,
    val nationality: String
)
