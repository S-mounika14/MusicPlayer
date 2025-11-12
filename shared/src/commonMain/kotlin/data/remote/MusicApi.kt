package com.musicplayer.shared.data.remote

import com.musicplayer.shared.data.model.DeezerResponse
import com.musicplayer.shared.data.model.ITunesResponse
import com.musicplayer.shared.data.model.Track
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.json.Json
import android.util.Log

class MusicApi(private val client: HttpClient) {

    private val deezerBaseUrl = "https://api.deezer.com"
    private val itunesBaseUrl = "https://itunes.apple.com"
    private val json = Json { ignoreUnknownKeys = true; isLenient = true; coerceInputValues = true }

    suspend fun getITunesTracks(term: String = "pop", limit: Int = 50): List<Track> {
        val url = "$itunesBaseUrl/search?term=$term&media=music&entity=song&limit=$limit"
        Log.d("MusicApi", "Fetching iTunes URL: $url")
        return try {
            val response: HttpResponse = client.get(url)
            val bodyText = response.bodyAsText()
            val itunesResponse = json.decodeFromString<ITunesResponse>(bodyText)
            itunesResponse.toTracks()
        } catch (e: Exception) {
            Log.e("MusicApi", "Error fetching iTunes tracks: ${e.message}")
            emptyList()
        }
    }

    suspend fun searchDeezerTracks(query: String = "pop"): DeezerResponse {
        val url = "$deezerBaseUrl/search?q=$query&limit=50"
        Log.d("MusicApi", "Fetching Deezer URL: $url")
        return try {
            val response: HttpResponse = client.get(url)
            val bodyText = response.bodyAsText()
            json.decodeFromString<DeezerResponse>(bodyText)
        } catch (e: Exception) {
            Log.e("MusicApi", "Error fetching Deezer tracks: ${e.message}")
            DeezerResponse()
        }
    }
}
