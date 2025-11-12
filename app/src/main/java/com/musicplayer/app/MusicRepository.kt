package com.musicplayer.app.data.repository

import android.util.Log
import com.musicplayer.app.data.model.Track
import com.musicplayer.app.data.remote.MusicApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MusicRepository(private val api: MusicApi) {

    suspend fun getTracks(): Result<List<Track>> = withContext(Dispatchers.IO) {
        try {
            Log.d("MusicRepository", "Fetching tracks from API...")

            val tracks = api.getITunesTracks("pop", 50)

            if (tracks.isNotEmpty()) {
                Log.d("MusicRepository", "iTunes API returned ${tracks.size} tracks")
                return@withContext Result.success(tracks)
            }

            Log.d("MusicRepository", "Trying Deezer API as fallback...")
            val deezerResponse = api.searchTracks("pop")

            if (deezerResponse.data.isNotEmpty()) {
                Log.d("MusicRepository", "Deezer API returned ${deezerResponse.data.size} tracks")
                return@withContext Result.success(deezerResponse.data)
            }

            Log.e("MusicRepository", "Both APIs returned empty results")
            Result.failure(Exception("No tracks available from any API"))

        } catch (e: Exception) {
            Log.e("MusicRepository", "Error fetching tracks", e)
            Result.failure(e)
        }
    }
}
