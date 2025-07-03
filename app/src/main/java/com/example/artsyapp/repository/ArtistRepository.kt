// app/src/main/java/com/example/artsyapp/repository/ArtistRepository.kt
package com.example.artsyapp.repository

import android.util.Log
import com.example.artsyapp.model.Href
import com.example.artsyapp.model.ArtistLinks
import com.example.artsyapp.model.Artwork
import com.example.artsyapp.model.Artist
import com.example.artsyapp.model.Favorite
import com.example.artsyapp.model.FavoriteRequest
import com.example.artsyapp.model.Gene
import com.example.artsyapp.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ArtistRepository {
    suspend fun searchArtists(query: String): List<Artist> = withContext(Dispatchers.IO) {
        val q = query.trim()
        if (q.isBlank()) return@withContext emptyList()

        val resp = RetrofitInstance.api.searchArtists(q)
        if (!resp.isSuccessful) {
            Log.e("ArtistRepo", "search failed: ${resp.code()}")
            return@withContext emptyList()
        }
        val hits = resp.body()?.embedded?.results.orEmpty()
        hits.mapNotNull { hit ->
            val name = hit.name ?: return@mapNotNull null

            // 1) If links is null, skip this hit
            val links = hit.links ?: return@mapNotNull null

            // now links is non-null, so you can safely do:
            val selfHref  = links.self.href
            val thumbHref = links.thumbnail.href

            Artist(
                id          = selfHref.substringAfterLast("/"),
                name        = name,
                nationality = null,
                birthday    = null,
                links      = ArtistLinks(thumbnail = Href(thumbHref))


            )
        }
    }

    fun Favorite.toArtist(): Artist = Artist(
        id = artistId,
        name = artistName,
        nationality = nationality,
        birthday = birthYear?.toString(),
        addedTime = favoritedAt,
        links = ArtistLinks(thumbnail = Href(artistImageURL)) // 👈 set thumbnail href manually
    )

    suspend fun getFavorites(): List<Artist> = withContext(Dispatchers.IO) {
        val resp = RetrofitInstance.api.getFavoriteArtists()
        if (!resp.isSuccessful) {
            Log.e("ArtistRepo", "getFavorites failed: ${resp.code()}")
            throw Exception("Favorites API failed: ${resp.code()}")
        }
        resp.body()?.favorites.orEmpty().map { fav ->
            Artist(
                id          = fav.artistId,
                name        = fav.artistName,
                nationality = fav.nationality,
                birthday    = fav.birthYear?.toString(),
                addedTime   = fav.favoritedAt,
            )
        }
    }

    suspend fun getArtist(id: String): Artist? = withContext(Dispatchers.IO) {
        val resp = RetrofitInstance.api.getArtistById(id)
        if (!resp.isSuccessful) {
            Log.e("ArtistRepo", "getArtistById failed: ${resp.code()}")
            return@withContext null
        }
        resp.body()
    }

    suspend fun getArtworks(artistId: String): List<Artwork> = withContext(Dispatchers.IO) {
        try {
            Log.d("ArtistRepo", "Getting artworks for artist ID: $artistId")
            val resp = RetrofitInstance.api.getArtworks(artistId)

            if (!resp.isSuccessful) {
                Log.e("ArtistRepo", "getArtworks failed: ${resp.code()}")
                return@withContext emptyList()
            }

            val artworks = resp.body()
                ?.embedded
                ?.artworks
                ?.filterNotNull() // Make sure no null artworks
                ?: emptyList()

            Log.d("ArtistRepo", "Received ${artworks.size} artworks for artist ID: $artistId")

            // Log the first few artwork titles to help debug
            artworks.take(3).forEach { artwork ->
                Log.d("ArtistRepo", "Artwork: ${artwork.title ?: "Untitled"}, ID: ${artwork.id}, Image: ${artwork.imageUrl}")
            }

            artworks
        } catch (e: Exception) {
            Log.e("ArtistRepo", "Exception getting artworks: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun getGenes(artworkId: String): List<Gene> = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitInstance.api.getGenes(mapOf("artwork_id" to artworkId))
            response.embedded?.genes ?: emptyList()
        } catch (e: Exception) {
            Log.e("ArtistRepo", "getGenes failed", e)
            emptyList()
        }
    }

    suspend fun favoriteArtist(artist: Artist) = withContext(Dispatchers.IO) {
        val body = FavoriteRequest(
            artistName = artist.name,
            artistImageURL = artist.imageUrl ?: "",
            birthYear = artist.birthday?.toIntOrNull(),
            deathYear = null, // you can extend later
            nationality = artist.nationality.orEmpty()
        )
        val resp = RetrofitInstance.api.favoriteArtist(artist.id, body)
        if (!resp.isSuccessful) {
            Log.e("ArtistRepo", "favoriteArtist failed: ${resp.code()}")
        }
    }

    suspend fun unfavoriteArtist(artistId: String) = withContext(Dispatchers.IO) {
        val resp = RetrofitInstance.api.unfavoriteArtist(artistId)
        if (!resp.isSuccessful) {
            Log.e("ArtistRepo", "unfavoriteArtist failed: ${resp.code()}")
        }
    }


    // In ArtistRepository.kt if using ArtsySearchResponse
    // In ArtistRepository.kt
    // In ArtistRepository.kt
    // In ArtistRepository.kt
    suspend fun getSimilarArtists(artistId: String): List<Artist> = withContext(Dispatchers.IO) {
        try {
            Log.d("ArtistRepo", "Fetching similar artists for $artistId")
            val response = RetrofitInstance.api.getSimilarArtists(artistId)

            if (!response.isSuccessful) {
                Log.e("ArtistRepo", "getSimilarArtists failed: ${response.code()}")
                return@withContext emptyList()
            }

            val artists = response.body()?.embedded?.artists ?: emptyList()
            Log.d("ArtistRepo", "Found ${artists.size} similar artists")

            artists.forEach { artist ->
                Log.d("ArtistRepo", "Similar artist: ${artist.name}, id: ${artist.id}")
            }

            return@withContext artists
        } catch (e: Exception) {
            Log.e("ArtistRepo", "Error fetching similar artists", e)
            emptyList()
        }
    }


}
