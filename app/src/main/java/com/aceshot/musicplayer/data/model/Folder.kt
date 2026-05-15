// FEATURE: Music Library & Scanning — Folder domain model
package com.aceshot.musicplayer.data.model

data class Folder(
    val path: String,
    val name: String,
    val songCount: Int
)
