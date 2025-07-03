package com.example.artsyapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.artsyapp.model.Artist
import com.example.artsyapp.network.TokenManager
import com.example.artsyapp.repository.ArtistRepository
import com.example.artsyapp.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val artistRepo: ArtistRepository = ArtistRepository(),
    private val userRepo: UserRepository = UserRepository()
) : ViewModel() {

    private val _favorites = MutableStateFlow<List<Artist>>(emptyList())
    val favorites: StateFlow<List<Artist>> = _favorites

    init {
        loadFavorites()
    }

    /** Fetch the user's favorites on startup (or pull‑to‑refresh). */
    fun loadFavorites() {
        viewModelScope.launch {
            val fetched = try {
                artistRepo.getFavorites()
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Failed to fetch favorites", e)
                null // ❗ return null if error
            }

            if (fetched != null) {
                Log.d("HomeViewModel", "Fetched ${fetched.size} favorites")
                _favorites.value = fetched
            } else {
                Log.e("HomeViewModel", "Keeping old favorites because fetch failed ❌")
                // Do NOT reset favorites to empty list on 401 or failure
            }
        }
    }

    fun isArtistFavorited(artistId: String): Boolean {
        return favorites.value.any { it.id == artistId }
    }

    fun toggleFavorite(artist: Artist) = viewModelScope.launch {
        val isFavorited = favorites.value.any { it.id == artist.id }
        if (!isFavorited) {
            artistRepo.favoriteArtist(artist)
        } else {
            artistRepo.unfavoriteArtist(artist.id)
        }
        loadFavorites()
    }

    /** Deletes the current user account */
    fun deleteUser() = viewModelScope.launch {
        val result = userRepo.deleteUser()
        Log.d("HomeViewModel", "User deletion result: $result")
        TokenManager.clearAll()
        _favorites.value = emptyList()
    }

    fun signOut() = viewModelScope.launch {
        userRepo.signOut()
        TokenManager.clearAll()
        _favorites.value = emptyList()   // ✅ Clear favorites after logout
    }

    fun isLoggedIn(): Boolean {
        // First check if tokens exist
        val hasTokens = !TokenManager.artsyToken.isNullOrEmpty() || !TokenManager.jwtToken.isNullOrEmpty()

        // For debugging
        Log.d("HomeViewModel", "isLoggedIn: hasTokens=$hasTokens, favorites=${favorites.value.size}")

        return hasTokens
    }
}
