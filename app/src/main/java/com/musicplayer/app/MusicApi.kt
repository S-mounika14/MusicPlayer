package com.musicplayer.app.data.remote

import android.util.Log
import com.musicplayer.app.data.model.DeezerResponse
import com.musicplayer.app.data.model.ITunesResponse
import com.musicplayer.app.data.model.Track
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.json.Json

class MusicApi(private val client: HttpClient) {

    private val deezerBaseUrl = "https://api.deezer.com"
    private val itunesBaseUrl = "https://itunes.apple.com"

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    suspend fun getITunesTracks(term: String = "pop", limit: Int = 50): List<Track> {
        val url = "$itunesBaseUrl/search?term=$term&media=music&entity=song&limit=$limit"
        Log.d("MusicApi", "Fetching from iTunes: $url")

        return try {
            val response: HttpResponse = client.get(url)
            val bodyText = response.bodyAsText()
            Log.d("MusicApi", "iTunes Response length: ${bodyText.length}")
            Log.d("MusicApi", "iTunes Response preview: ${bodyText.take(200)}")

            val itunesResponse = json.decodeFromString<ITunesResponse>(bodyText)
            val tracks = itunesResponse.toTracks()
            Log.d("MusicApi", "Successfully parsed ${tracks.size} iTunes tracks")

            tracks
        } catch (e: Exception) {
            Log.e("MusicApi", "Error fetching iTunes tracks: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun searchTracks(query: String = "pop"): DeezerResponse {
        val url = "$deezerBaseUrl/search?q=$query&limit=50"
        Log.d("MusicApi", "Fetching from Deezer: $url")

        return try {
            val response: HttpResponse = client.get(url)
            val bodyText = response.bodyAsText()
            Log.d("MusicApi", "Deezer Response length: ${bodyText.length}")
            Log.d("MusicApi", "Deezer Response preview: ${bodyText.take(200)}")

            // Manually parse JSON
            val deezerResponse = json.decodeFromString<DeezerResponse>(bodyText)
            Log.d("MusicApi", "Successfully parsed ${deezerResponse.data.size} Deezer tracks")

            deezerResponse
        } catch (e: Exception) {
            Log.e("MusicApi", "Error in searchTracks: ${e.message}", e)
            DeezerResponse(data = emptyList())
        }
    }
}