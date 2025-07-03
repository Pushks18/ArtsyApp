// app/src/main/java/com/example/artsyapp/model/Gene.kt
package com.example.artsyapp.model

import com.google.gson.annotations.SerializedName

// Use shared Href class
import com.example.artsyapp.model.Href

data class GenesResponse(
    @SerializedName("_embedded") val embedded: EmbeddedGenes?
)

data class EmbeddedGenes(
    val genes: List<Gene>
)

data class Gene(
    val id: String,
    val name: String,
    val description: String?,
    @SerializedName("_links") val links: GeneLinks
) {
    val thumbnailUrl: String get() = links.thumbnail.href
}

data class GeneLinks(
    val thumbnail: Href
)
