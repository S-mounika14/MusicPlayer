package com.musicplayer.shared.data.model

import kotlinx.serialization.Serializable

@Serializable
data class DeezerResponse(
    val data: List<Track> = emptyList(),
    val total: Int? = null
)
