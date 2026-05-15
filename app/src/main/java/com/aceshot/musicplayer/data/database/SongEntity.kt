// FEATURE: Music Library & Scanning — Room song entity
package com.aceshot.musicplayer.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "songs")
data class SongEntity(
    @PrimaryKey val id: Long,
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
    val uri: String,
    val trackNumber: Int = 0,
    val year: Int = 0
)
