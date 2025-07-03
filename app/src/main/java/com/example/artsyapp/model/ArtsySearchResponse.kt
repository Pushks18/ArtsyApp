// app/src/main/java/com/example/artsyapp/model/ArtsySearchResponse.kt
package com.example.artsyapp.model

import com.google.gson.annotations.SerializedName

data class ArtsySearchResponse(
    @SerializedName("_embedded") val embedded: Embedded?
)

data class Embedded(
    val results: List<SearchHit>? = null,  // For search responses
    val artists: List<Artist>? = null
)

data class SearchHit(
    @SerializedName("title")   val name: String?,
    @SerializedName("_links")  val links: SearchLinks?
)

data class SearchLinks(
    val self: Href,
    val permalink: Href,
    val thumbnail: Href
)