// app/src/main/java/com/example/artsyapp/model/FavoritesResponse.kt
package com.example.artsyapp.model
import com.google.gson.annotations.SerializedName

data class FavoritesResponse(
    @SerializedName("favorites") val favorites: List<Favorite>
)

data class Favorite(
    val artistId: String,
    val artistName: String,
    val artistImageURL: String,
    val birthYear: Int? = null,
    val deathYear: Int? = null,
    val nationality: String? = null,
    val favoritedAt: String
)
