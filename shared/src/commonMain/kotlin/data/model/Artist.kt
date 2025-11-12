package com.musicplayer.shared.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Artist(
    val id: Long? = null,
    val name: String,
    @SerialName("picture") val picture: String? = null,
    @SerialName("picture_medium") val pictureMedium: String? = null
)
