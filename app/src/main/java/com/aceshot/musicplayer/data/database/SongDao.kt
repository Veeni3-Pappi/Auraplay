// FEATURE: Music Library & Scanning — Song data access
package com.aceshot.musicplayer.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {
    // FEATURE: Library Views — All songs with sort options
    @Query("SELECT * FROM songs ORDER BY title ASC")
    fun getAllSongsByTitle(): Flow<List<SongEntity>>

    @Query("SELECT * FROM songs ORDER BY artist ASC, title ASC")
    fun getAllSongsByArtist(): Flow<List<SongEntity>>

    @Query("SELECT * FROM songs ORDER BY dateAdded DESC")
    fun getAllSongsByDateAdded(): Flow<List<SongEntity>>

    @Query("SELECT * FROM songs ORDER BY duration DESC")
    fun getAllSongsByDuration(): Flow<List<SongEntity>>

    // FEATURE: Library Views — Album grouping
    @Query("SELECT DISTINCT albumId, album AS name, artist, year, COUNT(*) AS songCount, 'content://media/external/audio/albumart/' || albumId AS albumArtUri FROM songs GROUP BY albumId ORDER BY album ASC")
    fun getAllAlbums(): Flow<List<AlbumResult>>

    // FEATURE: Library Views — Artist grouping
    @Query("SELECT DISTINCT artist AS name, COUNT(*) AS trackCount, COUNT(DISTINCT albumId) AS albumCount FROM songs GROUP BY artist ORDER BY artist ASC")
    fun getAllArtists(): Flow<List<ArtistResult>>

    // FEATURE: Library Views — Genre grouping
    @Query("SELECT DISTINCT genre AS name, COUNT(*) AS songCount FROM songs WHERE genre != '' GROUP BY genre ORDER BY genre ASC")
    fun getAllGenres(): Flow<List<GenreResult>>

    // FEATURE: Library Views — Folder grouping
    @Query("SELECT DISTINCT folderPath AS path, COUNT(*) AS songCount FROM songs GROUP BY folderPath ORDER BY folderPath ASC")
    fun getAllFolders(): Flow<List<FolderResult>>

    @Query("SELECT * FROM songs WHERE albumId = :albumId ORDER BY trackNumber ASC")
    fun getSongsByAlbum(albumId: Long): Flow<List<SongEntity>>

    @Query("SELECT * FROM songs WHERE artist = :artist ORDER BY title ASC")
    fun getSongsByArtist(artist: String): Flow<List<SongEntity>>

    @Query("SELECT * FROM songs WHERE genre = :genre ORDER BY title ASC")
    fun getSongsByGenre(genre: String): Flow<List<SongEntity>>

    @Query("SELECT * FROM songs WHERE folderPath = :folderPath ORDER BY title ASC")
    fun getSongsByFolder(folderPath: String): Flow<List<SongEntity>>

    @Query("SELECT * FROM songs WHERE id = :id LIMIT 1")
    suspend fun getSongById(id: Long): SongEntity?

    // FEATURE: Search — Full text search
    @Query("SELECT songs.* FROM songs JOIN songs_fts ON songs.rowid = songs_fts.rowid WHERE songs_fts MATCH :query")
    fun searchSongs(query: String): Flow<List<SongEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(songs: List<SongEntity>)

    @Query("DELETE FROM songs")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM songs")
    suspend fun getSongCount(): Int
}

// FEATURE: Library Views — Query result types
data class AlbumResult(
    val albumId: Long,
    val name: String,
    val artist: String,
    val year: Int,
    val songCount: Int,
    val albumArtUri: String?
)

data class ArtistResult(
    val name: String,
    val trackCount: Int,
    val albumCount: Int
)

data class GenreResult(
    val name: String,
    val songCount: Int
)

data class FolderResult(
    val path: String,
    val songCount: Int
)
