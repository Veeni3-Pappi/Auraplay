// FEATURE: Music Library & Scanning — Album domain model
package com.aceshot.musicplayer.data.model

data class Album(
    val id: Long,
    val name: String,
    val artist: String,
    val year: Int,
    val songCount: Int,
    val albumArtUri: String?
)
