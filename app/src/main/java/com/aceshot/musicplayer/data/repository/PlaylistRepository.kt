package com.aceshot.musicplayer.data.repository

import com.aceshot.musicplayer.data.database.PlaylistDao
import com.aceshot.musicplayer.data.database.PlaylistEntity
import com.aceshot.musicplayer.data.database.PlaylistSongCrossRef
import com.aceshot.musicplayer.data.model.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistRepository @Inject constructor(
    private val playlistDao: PlaylistDao
) {
    fun getAllPlaylists(): Flow<List<PlaylistEntity>> = playlistDao.getAllPlaylists()

    suspend fun createPlaylist(name: String): Long {
        return playlistDao.insertPlaylist(PlaylistEntity(name = name))
    }

    suspend fun deletePlaylist(playlist: PlaylistEntity) {
        playlistDao.deletePlaylist(playlist)
    }

    fun getPlaylistSongs(playlistId: Long): Flow<List<Song>> {
        return playlistDao.getPlaylistSongs(playlistId).map { list ->
            list.map { it.toDomainModel() }
        }
    }

    suspend fun addSongToPlaylist(playlistId: Long, songId: Long, position: Int = 0) {
        playlistDao.insertPlaylistSong(
            PlaylistSongCrossRef(
                playlistId = playlistId,
                songId = songId,
                position = position
            )
        )
    }

    suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long) {
        playlistDao.removePlaylistSong(playlistId, songId)
    }

    suspend fun ensureFavoritesPlaylistExists() {
        if (playlistDao.getFavoritesPlaylist() == null) {
            playlistDao.insertPlaylist(PlaylistEntity(name = "Favorites", isBuiltIn = true))
        }
    }

    suspend fun toggleFavorite(songId: Long) {
        val favPlaylist = playlistDao.getFavoritesPlaylist() ?: return
        val isFav = isFavorite(songId).first()
        
        if (isFav) {
            removeSongFromPlaylist(favPlaylist.id, songId)
        } else {
            val count = playlistDao.getPlaylistSongCount(favPlaylist.id)
            addSongToPlaylist(favPlaylist.id, songId, position = count)
        }
    }

    fun isFavorite(songId: Long): Flow<Boolean> = playlistDao.isFavorite(songId)
}
