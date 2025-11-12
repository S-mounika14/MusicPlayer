package com.musicplayer.app.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musicplayer.app.data.model.Track
import com.musicplayer.app.data.repository.MusicRepository
import kotlinx.coroutines.launch

class MusicViewModel(
    private val repository: MusicRepository
) : ViewModel() {

    private val _tracks = MutableLiveData<List<Track>>()
    val tracks: LiveData<List<Track>> = _tracks

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _selectedTrack = MutableLiveData<Track?>()
    val selectedTrack: LiveData<Track?> = _selectedTrack

    private var originalTracks = listOf<Track>()

    fun loadTracks() {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                repository.getTracks()
                    .onSuccess { trackList ->
                        Log.d("MusicViewModel", "Successfully loaded ${trackList.size} tracks")
                        originalTracks = trackList
                        _tracks.value = trackList
                        _isLoading.value = false

                        if (trackList.isEmpty()) {
                            _error.value = "No tracks found. Please check your internet connection."
                        }
                    }
                    .onFailure { exception ->
                        Log.e("MusicViewModel", "Failed to load tracks", exception)
                        _error.value = "Failed to load tracks: ${exception.message}"
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                Log.e("MusicViewModel", "Unexpected error", e)
                _error.value = "Unexpected error: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    fun sortByName() {
        _tracks.value = _tracks.value?.sortedBy { it.title }
    }

    fun sortByDuration() {
        _tracks.value = _tracks.value?.sortedBy { it.duration }
    }

    fun selectTrack(track: Track) {
        _selectedTrack.value = track
    }
}
