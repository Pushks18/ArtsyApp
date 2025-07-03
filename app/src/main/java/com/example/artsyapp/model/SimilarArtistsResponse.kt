package com.example.artsyapp.model

import com.google.gson.annotations.SerializedName

data class SimilarArtistsResponse(
    @SerializedName("_embedded") val embedded: SimilarArtistsEmbedded
)

data class SimilarArtistsEmbedded(
    val artists: List<Artist>
)