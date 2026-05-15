// FEATURE: Music Library & Scanning — Song domain model
package com.aceshot.musicplayer.data.model

import android.net.Uri

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val albumId: Long,
    val genre: String,
    val duration: Long,
    val dateAdded: Long,
    val path: String,
    val fileName: String,
    val folderPath: String,
    val uri: Uri,
    val trackNumber: Int = 0,
    val year: Int = 0
)
