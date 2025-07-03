// app/src/main/java/com/example/artsyapp/viewmodel/ArtistDetailViewModel.kt
package com.example.artsyapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.artsyapp.model.Artwork
import com.example.artsyapp.model.Artist
import com.example.artsyapp.repository.ArtistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// In ArtistDetailViewModel.kt
class ArtistDetailViewModel(
    private val repository: ArtistRepository = ArtistRepository()
) : ViewModel() {

    private val _artist = MutableStateFlow<Artist?>(null)
    val artist: StateFlow<Artist?> = _artist

    private val _artworks = MutableStateFlow<List<Artwork>>(emptyList())
    val artworks: StateFlow<List<Artwork>> = _artworks

    private val _similar = MutableStateFlow<List<Artist>>(emptyList())
    val similar: StateFlow<List<Artist>> = _similar

    fun loadArtist(id: String) {
        Log.d("ArtistDetailViewModel", "Loading artist with ID: $id")

        viewModelScope.launch {
            // Load artist
            val artist = repository.getArtist(id)
            _artist.value = artist

            // Load artworks with clear logging
            Log.d("ArtistDetailViewModel", "Now loading artworks for artist ID: $id")
            val fetchedArtworks = repository.getArtworks(id)
            Log.d("ArtistDetailViewModel", "Loaded ${fetchedArtworks.size} artworks for artist ID: $id")
            _artworks.value = fetchedArtworks

            // Load similar artists
            Log.d("ArtistDetailViewModel", "Now loading similar artists for artist ID: $id")
            val similarArtists = repository.getSimilarArtists(id)
            Log.d("ArtistDetailViewModel", "Loaded ${similarArtists.size} similar artists")
            _similar.value = similarArtists
        }
    }
}