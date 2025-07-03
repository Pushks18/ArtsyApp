// app/src/main/java/com/example/artsyapp/viewmodel/SearchViewModel.kt
package com.example.artsyapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.artsyapp.model.Artist
import com.example.artsyapp.repository.ArtistRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.OptIn

@OptIn(FlowPreview::class)
class SearchViewModel(
    private val repo: ArtistRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _results = MutableStateFlow<List<Artist>>(emptyList())
    val results: StateFlow<List<Artist>> = _results

    init {
        _query
            .debounce(300L)
            .distinctUntilChanged()
            // only fire when user has typed 3 or more chars
            .filter { it.length >= 3 }
            .onEach { q ->
                val list = repo.searchArtists(q)
                _results.value = list
            }
            .launchIn(viewModelScope)
    }

    fun onQueryChange(new: String) {
        _query.value = new
        // clear results if they delete back below 3 chars
        if (new.length < 3) {
            _results.value = emptyList()
        }
    }
    fun clearSearch() {
        _query.value = ""
        _results.value = emptyList()
    }
}
