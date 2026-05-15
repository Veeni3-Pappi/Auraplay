// FEATURE: Music Library & Scanning — Artist domain model
package com.aceshot.musicplayer.data.model

data class Artist(
    val id: Long,
    val name: String,
    val trackCount: Int,
    val albumCount: Int
)
