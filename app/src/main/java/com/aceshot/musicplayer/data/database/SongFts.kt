// FEATURE: Search — FTS virtual table for full-text search
package com.aceshot.musicplayer.data.database

import androidx.room.Entity
import androidx.room.Fts4

@Fts4(contentEntity = SongEntity::class)
@Entity(tableName = "songs_fts")
data class SongFts(
    val title: String,
    val artist: String,
    val album: String,
    val genre: String,
    val fileName: String
)
