package com.musicplayer.shared.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
            duration = trackTime / 1000,
            previewUrl = previewUrl,
            artist = Artist(name = artistName),
            album = Album(coverMedium = artwork)
        )
    }
}
