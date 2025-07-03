package com.example.artsyapp.model

import com.google.gson.annotations.SerializedName
import java.time.Duration
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

data class Artist(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("nationality") val nationality: String? = null,
    @SerializedName("biography") val description: String? = null,
    @SerializedName("birthday") val birthday: String? = null,
    @SerializedName("deathday") val deathday: String? = null,
    val addedTime: String? = null,
    @SerializedName("_links") val links: ArtistLinks? = null
) {
    val imageUrl: String? get() = links?.thumbnail?.href
    val relativeTime: String get() = getRelativeTime(addedTime)
}

fun Favorite.toArtist(): Artist = Artist(
    id = artistId,
    name = artistName,
    nationality = nationality,
    birthday = birthYear?.toString(),
    addedTime = favoritedAt,
    links = ArtistLinks(thumbnail = Href(artistImageURL))
)

data class ArtistLinks(
    val thumbnail: Href
)

// Remove these classes - they're causing the redeclaration error
// data class SimilarArtistsResponse(...)
// data class SimilarArtistsEmbedded(...)

fun getRelativeTime(isoTime: String?): String {
    if (isoTime == null) return ""
    return try {
        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        val pastTime = OffsetDateTime.parse(isoTime, formatter)
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val duration = Duration.between(pastTime, now)

        when {
            duration.seconds < 60 -> "${duration.seconds} seconds ago"
            duration.toMinutes() < 60 -> "${duration.toMinutes()} minutes ago"
            duration.toHours() < 24 -> "${duration.toHours()} hours ago"
            else -> "${duration.toDays()} days ago"
        }
    } catch (e: Exception) {
        ""
    }
}