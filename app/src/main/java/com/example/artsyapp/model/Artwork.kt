// app/src/main/java/com/example/artsyapp/model/Artwork.kt
package com.example.artsyapp.model

import com.google.gson.annotations.SerializedName

// Use shared Href class
import com.example.artsyapp.model.Href

data class ArtworksResponse(
    @SerializedName("_embedded") val embedded: ArtworksEmbedded
)

data class ArtworksEmbedded(
    val artworks: List<Artwork>
)

data class Artwork(
    val id: String,
    val title: String?,
    val date: String?,
    @SerializedName("_links") private val links: ArtworkLinks
) {
    // expose the thumbnail URL
    val imageUrl: String
        get() = links?.thumbnail?.href ?: ""
}

data class ArtworkLinks(
    val thumbnail: Href?
)