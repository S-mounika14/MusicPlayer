package com.musicplayer.shared.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ITunesResponse(
    val resultCount: Int,
    val results: List<ITunesTrack>
) {
    fun toTracks(): List<Track> = results.filter { it.kind == "song" }.map { it.toTrack() }
}
