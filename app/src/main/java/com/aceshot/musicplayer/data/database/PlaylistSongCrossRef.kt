// FEATURE: Playlists — Playlist-Song relationship
package com.aceshot.musicplayer.data.database

import androidx.room.Entity

@Entity(
    tableName = "playlist_songs",
    primaryKeys = ["playlistId", "songId"]
)
data class PlaylistSongCrossRef(
    val playlistId: Long,
    val songId: Long,
    val position: Int,
    val addedAt: Long = System.currentTimeMillis()
)
