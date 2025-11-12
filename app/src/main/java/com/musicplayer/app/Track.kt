package com.musicplayer.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Deezer Models
@Serializable
data class DeezerResponse(
    val data: List<Track> = emptyList(),
    val total: Int? = null
)

// iTunes Models
@Serializable
data class ITunesResponse(
    val resultCount: Int,
    val results: List<ITunesTrack>
) {
    fun toTracks(): List<Track> {
        return results.filter { it.kind == "song" }.map { it.toTrack() }
    }
}

@Serializable
data class ITunesTrack(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    @SerialName("trackTimeMillis") val trackTime: Int,
    val previewUrl: String,
    @SerialName("artworkUrl100") val artwork: String,
    val kind: String? = null
) {
    fun toTrack(): Track {
        return Track(
            id = trackId,
            title = trackName,
            duration = trackTime / 1000, // Convert to seconds
            previewUrl = previewUrl,
            artist = Artist(name = artistName),
            album = Album(coverMedium = artwork)
        )
    }
}

@Serializable
data class Track(
    val id: Long,
    val title: String,
    val duration: Int, // in seconds
    val previewUrl: String,
    val artist: Artist,
    val album: Album? = null
) {
    fun getDurationFormatted(): String {
        val minutes = duration / 60
        val seconds = duration % 60
        return String.format("%d:%02d", minutes, seconds)
    }
}

@Serializable
data class Artist(
    val id: Long? = null,
    val name: String,
    @SerialName("picture") val picture: String? = null,
    @SerialName("picture_medium") val pictureMedium: String? = null
)

@Serializable
data class Album(
    val id: Long? = null,
    val title: String? = null,
    @SerialName("cover") val cover: String? = null,
    @SerialName("cover_small") val coverSmall: String? = null,
    @SerialName("cover_medium") val coverMedium: String? = null,
    @SerialName("cover_big") val coverBig: String? = null
)