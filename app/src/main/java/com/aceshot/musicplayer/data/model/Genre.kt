// FEATURE: Music Library & Scanning — Genre domain model
package com.aceshot.musicplayer.data.model

data class Genre(
    val id: Long,
    val name: String,
    val songCount: Int
)
