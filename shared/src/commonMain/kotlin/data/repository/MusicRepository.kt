package com.musicplayer.shared.data.repository

import com.musicplayer.shared.data.model.Track
import com.musicplayer.shared.data.remote.MusicApi

class MusicRepository(private val api: MusicApi) {

    suspend fun getTracks(): Result<List<Track>> {
        return try {
            val tracks = api.getITunesTracks() // or api.searchDeezerTracks()
            Result.success(tracks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
