package com.aceshot.musicplayer.data.repository

import android.net.Uri
import com.aceshot.musicplayer.data.database.SongDao
import com.aceshot.musicplayer.data.database.SongEntity
import com.aceshot.musicplayer.data.model.Album
import com.aceshot.musicplayer.data.model.Artist
import com.aceshot.musicplayer.data.model.Folder
import com.aceshot.musicplayer.data.model.Genre
import com.aceshot.musicplayer.data.model.Song
import com.aceshot.musicplayer.data.preferences.UserPreferencesManager
import com.aceshot.musicplayer.data.scanner.FolderExclusionManager
import com.aceshot.musicplayer.data.scanner.MediaStoreScanner
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

enum class SortOrder { TITLE, ARTIST, DATE_ADDED, DURATION }

@Singleton
class MusicRepository @Inject constructor(
    private val songDao: SongDao,
    private val scanner: MediaStoreScanner,
    private val folderManager: FolderExclusionManager,
    private val prefsManager: UserPreferencesManager
) {
    suspend fun scanAndSync() {
        folderManager.detectAndSeedExclusions()
        val prefs = prefsManager.preferencesFlow.first()
        val excluded = folderManager.getExcludedPaths()
        val newSongs = scanner.scanAudioFiles(prefs.minDurationFilterMs, excluded)

        songDao.deleteAll()
        songDao.insertAll(newSongs)
        prefsManager.updateLastScanTimestamp(System.currentTimeMillis())
    }

    fun getAllSongs(sortOrder: SortOrder = SortOrder.TITLE): Flow<List<Song>> {
        val entityFlow = when (sortOrder) {
            SortOrder.TITLE -> songDao.getAllSongsByTitle()
            SortOrder.ARTIST -> songDao.getAllSongsByArtist()
            SortOrder.DATE_ADDED -> songDao.getAllSongsByDateAdded()
            SortOrder.DURATION -> songDao.getAllSongsByDuration()
        }
        return entityFlow.map { list -> list.map { it.toDomainModel() } }
    }

    fun getAlbums(): Flow<List<Album>> = songDao.getAllAlbums().map { list ->
        list.map { Album(it.albumId, it.name, it.artist, it.year, it.songCount, it.albumArtUri) }
    }

    fun getArtists(): Flow<List<Artist>> = songDao.getAllArtists().map { list ->
        list.map { Artist(0L, it.name, it.trackCount, it.albumCount) }
    }

    fun getGenres(): Flow<List<Genre>> = songDao.getAllGenres().map { list ->
        list.map { Genre(0L, it.name, it.songCount) }
    }

    fun getFolders(): Flow<List<Folder>> = songDao.getAllFolders().map { list ->
        list.map { Folder(it.path, it.path.substringAfterLast("/"), it.songCount) }
    }

    fun getSongsByAlbum(albumId: Long): Flow<List<Song>> = 
        songDao.getSongsByAlbum(albumId).map { list -> list.map { it.toDomainModel() } }

    fun getSongsByArtist(artist: String): Flow<List<Song>> = 
        songDao.getSongsByArtist(artist).map { list -> list.map { it.toDomainModel() } }

    fun getSongsByGenre(genre: String): Flow<List<Song>> = 
        songDao.getSongsByGenre(genre).map { list -> list.map { it.toDomainModel() } }

    fun getSongsByFolder(folderPath: String): Flow<List<Song>> = 
        songDao.getSongsByFolder(folderPath).map { list -> list.map { it.toDomainModel() } }

    fun searchSongs(query: String): Flow<List<Song>> = 
        songDao.searchSongs("$query*").map { list -> list.map { it.toDomainModel() } }
}

fun SongEntity.toDomainModel() = Song(
    id = id,
    title = title,
    artist = artist,
    album = album,
    albumId = albumId,
    genre = genre,
    duration = duration,
    dateAdded = dateAdded,
    path = path,
    fileName = fileName,
    folderPath = folderPath,
    uri = Uri.parse(uri),
    trackNumber = trackNumber,
    year = year
)
