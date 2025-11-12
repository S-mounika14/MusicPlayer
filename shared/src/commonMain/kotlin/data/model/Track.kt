package com.musicplayer.shared.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Track(
    val id: Long,
    val title: String,
    val duration: Int, // seconds
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
