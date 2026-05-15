// FEATURE: Playlists — Room playlist entity
package com.aceshot.musicplayer.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isBuiltIn: Boolean = false,
    val coverImageUri: String? = null
)
